package com.chargescopixel.app.data.repository

import android.content.Context
import com.chargescopixel.app.data.local.AppDatabase
import com.chargescopixel.app.data.local.BatterySampleEntity
import com.chargescopixel.app.data.local.ChargingSessionEntity
import com.chargescopixel.app.domain.BatterySnapshot
import com.chargescopixel.app.domain.InsightsSummary
import com.chargescopixel.app.domain.PlugType
import com.chargescopixel.app.domain.SessionMetrics
import com.chargescopixel.app.domain.SessionWithMetrics
import com.chargescopixel.app.utils.CsvExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.math.max

class ChargingRepository(
    private val context: Context,
    private val database: AppDatabase,
    private val settingsRepository: SettingsRepository
) {
    private val sessionDao = database.chargingSessionDao()
    private val sampleDao = database.batterySampleDao()

    val settings = settingsRepository.settings

    fun observeRecentSamples(limit: Int = 60): Flow<List<BatterySampleEntity>> =
        sampleDao.observeLatestSamples(limit)

    fun observeSessionsWithMetrics(): Flow<List<SessionWithMetrics>> =
        sessionDao.observeAllSessions().map { sessions ->
            sessions.map { session ->
                val samples = sampleDao.getSamplesForSessionOnce(session.id)
                SessionWithMetrics(
                    session = session,
                    metrics = computeMetrics(session, samples),
                    samples = samples
                )
            }
        }

    fun observeDashboardSummary(): Flow<InsightsSummary> =
        observeSessionsWithMetrics().map { sessionsWithMetrics ->
            if (sessionsWithMetrics.isEmpty()) {
                return@map InsightsSummary(
                    totalSessions = 0,
                    totalChargePercentGained = 0,
                    estimatedCycles = 0.0,
                    averageSessionGain = 0.0,
                    averageSessionDurationMinutes = 0.0,
                    hottestSessionTempC = 0f
                )
            }

            val totalGain = sessionsWithMetrics.sumOf { it.metrics.batteryGained }
            val totalDurationMinutes = sessionsWithMetrics.sumOf {
                it.metrics.durationMs / 60000.0
            }

            InsightsSummary(
                totalSessions = sessionsWithMetrics.size,
                totalChargePercentGained = totalGain,
                estimatedCycles = totalGain / 100.0,
                averageSessionGain = totalGain.toDouble() / sessionsWithMetrics.size,
                averageSessionDurationMinutes = totalDurationMinutes / sessionsWithMetrics.size,
                hottestSessionTempC = sessionsWithMetrics.maxOf { it.metrics.peakTempC }
            )
        }

    suspend fun startSessionIfNeeded(snapshot: BatterySnapshot): Long = withContext(Dispatchers.IO) {
        val openSession = sessionDao.getOpenSession()
        if (openSession != null) return@withContext openSession.id

        val session = ChargingSessionEntity(
            startTimeMs = snapshot.timestamp,
            startBatteryPercent = snapshot.batteryPercent,
            startTempC = snapshot.temperatureC,
            peakTempC = snapshot.temperatureC
        )
        sessionDao.insert(session)
    }

    suspend fun addSample(snapshot: BatterySnapshot): Long = withContext(Dispatchers.IO) {
        val sessionId = startSessionIfNeeded(snapshot)
        sampleDao.insert(
            BatterySampleEntity(
                sessionId = sessionId,
                timestampMs = snapshot.timestamp,
                batteryPercent = snapshot.batteryPercent,
                chargingStatus = snapshot.chargingStatus,
                health = snapshot.health,
                technology = snapshot.technology,
                plugType = snapshot.plugType.name,
                temperatureC = snapshot.temperatureC,
                voltageMv = snapshot.voltageMv,
                currentNowUa = snapshot.currentNowUa,
                chargeCounterUah = snapshot.chargeCounterUah,
                cycleCount = snapshot.cycleCount,
                thermalStatus = snapshot.thermalStatus
            )
        )

        val openSession = sessionDao.getOpenSession()
        if (openSession != null && snapshot.temperatureC > openSession.peakTempC) {
            sessionDao.update(openSession.copy(peakTempC = snapshot.temperatureC))
        }

        sessionId
    }

    suspend fun closeOpenSession(snapshot: BatterySnapshot? = null) = withContext(Dispatchers.IO) {
        val openSession = sessionDao.getOpenSession() ?: return@withContext
        val finalSample = snapshot ?: sampleDao.getSamplesForSessionOnce(openSession.id).lastOrNull()?.let {
            BatterySnapshot(
                timestamp = it.timestampMs,
                batteryPercent = it.batteryPercent,
                chargingStatus = it.chargingStatus,
                health = it.health,
                technology = it.technology,
                plugType = runCatching { PlugType.valueOf(it.plugType) }.getOrElse { PlugType.UNKNOWN },
                temperatureC = it.temperatureC,
                voltageMv = it.voltageMv,
                currentNowUa = it.currentNowUa,
                chargeCounterUah = it.chargeCounterUah,
                cycleCount = it.cycleCount,
                thermalStatus = it.thermalStatus
            )
        }

        sessionDao.update(
            openSession.copy(
                endTimeMs = finalSample?.timestamp ?: System.currentTimeMillis(),
                endBatteryPercent = finalSample?.batteryPercent,
                endTempC = finalSample?.temperatureC,
                peakTempC = max(openSession.peakTempC, finalSample?.temperatureC ?: openSession.peakTempC)
            )
        )
    }

    suspend fun exportCsv(): android.net.Uri = withContext(Dispatchers.IO) {
        val sessions = sessionDao.getAllSessionsOnce()
        val samples = sampleDao.getAllSamplesOnce()
        CsvExporter.exportSessionsAndSamples(context, sessions, samples)
    }

    fun computeMetrics(session: ChargingSessionEntity, samples: List<BatterySampleEntity>): SessionMetrics {
        val endTime = session.endTimeMs ?: samples.lastOrNull()?.timestampMs ?: System.currentTimeMillis()
        val duration = (endTime - session.startTimeMs).coerceAtLeast(0)

        val endBattery = session.endBatteryPercent ?: samples.lastOrNull()?.batteryPercent ?: session.startBatteryPercent
        val gained = (endBattery - session.startBatteryPercent).coerceAtLeast(0)
        val durationMinutes = duration / 60000.0

        val peakTemp = max(session.peakTempC, samples.maxOfOrNull { it.temperatureC } ?: session.startTempC)
        val tempRise = (peakTemp - session.startTempC).coerceAtLeast(0f)

        val avgVoltage = samples.map { it.voltageMv }.average().takeIf { !it.isNaN() } ?: 0.0
        val avgCurrent = samples.mapNotNull { it.currentNowUa }.average().takeIf { !it.isNaN() }

        return SessionMetrics(
            durationMs = duration,
            batteryGained = gained,
            percentPerMinute = if (durationMinutes > 0.0) gained / durationMinutes else 0.0,
            peakTempC = peakTemp,
            tempRiseC = tempRise,
            averageVoltageMv = avgVoltage,
            averageCurrentUa = avgCurrent,
            cycleContribution = gained / 100.0
        )
    }
}
