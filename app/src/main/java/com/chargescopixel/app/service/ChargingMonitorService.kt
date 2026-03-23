package com.chargescopixel.app.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.chargescopixel.app.ChargeScopeApplication
import com.chargescopixel.app.R
import com.chargescopixel.app.utils.BatteryReader
import com.chargescopixel.app.utils.NotificationUtils
import com.chargescopixel.app.widget.ChargeScopeWidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChargingMonitorService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var monitoringJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        NotificationUtils.ensureChannels(this)
        Log.d(TAG, "ChargingMonitorService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand action=${intent?.action}")
        when (intent?.action) {
            ACTION_STOP -> {
                stopMonitoringAndSelf()
                return START_NOT_STICKY
            }
        }

        startForeground(NOTIFICATION_ID, NotificationUtils.buildMonitorNotification(this))
        if (monitoringJob?.isActive == true) return START_STICKY

        monitoringJob = serviceScope.launch {
            if (!BatteryReader.isPluggedIn(this@ChargingMonitorService)) {
                Log.d(TAG, "Device is not plugged in; stopping monitor service")
                stopMonitoringAndSelf()
                return@launch
            }

            val repository = (application as ChargeScopeApplication).appContainer.chargingRepository
            val settingsRepository = (application as ChargeScopeApplication).appContainer.settingsRepository

            BatteryReader.readSnapshot(this@ChargingMonitorService)?.let { repository.startSessionIfNeeded(it) }

            while (true) {
                if (!BatteryReader.isPluggedIn(this@ChargingMonitorService)) {
                    Log.d(TAG, "Unplug detected in monitor loop; closing session")
                    repository.closeOpenSession(BatteryReader.readSnapshot(this@ChargingMonitorService))
                    stopMonitoringAndSelf()
                    break
                }

                val snapshot = BatteryReader.readSnapshot(this@ChargingMonitorService)
                if (snapshot != null) {
                    repository.addSample(snapshot)
                    ChargeScopeWidgetUpdater.requestRefresh(this@ChargingMonitorService)
                    Log.d(
                        TAG,
                        "Sample saved: ${snapshot.batteryPercent}% temp=${snapshot.temperatureC}C " +
                            "voltage=${snapshot.voltageMv}mV current=${snapshot.currentNowUa}"
                    )
                    val settings = settingsRepository.settings.first()
                    if (settings.alertsEnabled) {
                        maybeNotifyOverheating(snapshot.temperatureC, settings.overheatThresholdC)
                        maybeNotifySlowCharging(repository, settings.slowChargeThresholdPercentPerMinute)
                    }
                } else {
                    Log.w(TAG, "Battery snapshot unavailable; skipping sample")
                }

                delay(SAMPLE_INTERVAL_MS)
            }
        }

        return START_STICKY
    }

    private fun maybeNotifyOverheating(tempC: Float, threshold: Float) {
        if (tempC < threshold) return
        NotificationUtils.showAlert(
            context = this,
            title = getString(R.string.overheat_alert_title),
            message = "Battery at ${"%.1f".format(tempC)}°C (threshold ${"%.1f".format(threshold)}°C)",
            id = OVERHEAT_ALERT_ID
        )
    }

    private suspend fun maybeNotifySlowCharging(
        repository: com.chargescopixel.app.data.repository.ChargingRepository,
        thresholdPercentPerMinute: Float
    ) {
        val sessions = repository.observeSessionsWithMetrics().first()
        val latest = sessions.firstOrNull() ?: return
        if (latest.session.endTimeMs != null) return

        if (latest.metrics.percentPerMinute in 0.0..thresholdPercentPerMinute.toDouble()) {
            NotificationUtils.showAlert(
                context = this,
                title = getString(R.string.slow_charge_alert_title),
                message = "Current charging speed ${"%.2f".format(latest.metrics.percentPerMinute)}%/min",
                id = SLOW_ALERT_ID
            )
        }
    }

    private fun stopMonitoringAndSelf() {
        monitoringJob?.cancel()
        serviceScope.launch {
            val repository = (application as ChargeScopeApplication).appContainer.chargingRepository
            repository.closeOpenSession(BatteryReader.readSnapshot(this@ChargingMonitorService))
            ChargeScopeWidgetUpdater.requestRefresh(this@ChargingMonitorService)
            Log.d(TAG, "Session closed; stopping foreground service")
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "ChargingMonitorService destroyed")
        monitoringJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val ACTION_START = "com.chargescopixel.app.START_MONITOR"
        private const val ACTION_STOP = "com.chargescopixel.app.STOP_MONITOR"
        private const val NOTIFICATION_ID = 2001
        private const val OVERHEAT_ALERT_ID = 3001
        private const val SLOW_ALERT_ID = 3002
        private const val SAMPLE_INTERVAL_MS = 10_000L
        private const val TAG = "ChargingMonitorService"

        fun start(context: Context) {
            val intent = Intent(context, ChargingMonitorService::class.java).apply { action = ACTION_START }
            Log.d(TAG, "Requesting monitor start")
            runCatching { ContextCompat.startForegroundService(context, intent) }
                .onFailure { Log.e(TAG, "Failed to start foreground monitor service", it) }
        }

        fun stop(context: Context) {
            Log.d(TAG, "Requesting monitor stop")
            val intent = Intent(context, ChargingMonitorService::class.java).apply { action = ACTION_STOP }
            runCatching { context.startService(intent) }
                .onFailure {
                    Log.w(TAG, "Stop action startService failed, falling back to stopService", it)
                    context.stopService(Intent(context, ChargingMonitorService::class.java))
                }
        }

        fun ensureMonitoringMatchesCurrentPowerState(context: Context) {
            runCatching {
                if (BatteryReader.isPluggedIn(context)) {
                    start(context)
                } else {
                    stop(context)
                }
            }.onFailure {
                Log.e(TAG, "Failed to match monitoring state to power state", it)
            }
        }
    }
}
