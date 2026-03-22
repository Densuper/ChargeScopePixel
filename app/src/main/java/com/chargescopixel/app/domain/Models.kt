package com.chargescopixel.app.domain

import com.chargescopixel.app.data.local.BatterySampleEntity
import com.chargescopixel.app.data.local.ChargingSessionEntity

enum class PlugType {
    AC,
    USB,
    WIRELESS,
    UNKNOWN
}

data class BatterySnapshot(
    val timestamp: Long,
    val batteryPercent: Int,
    val chargingStatus: String,
    val plugType: PlugType,
    val temperatureC: Float,
    val voltageMv: Int,
    val currentNowUa: Int?,
    val chargeCounterUah: Int?,
    val thermalStatus: Int?
)

data class SessionMetrics(
    val durationMs: Long,
    val batteryGained: Int,
    val percentPerMinute: Double,
    val peakTempC: Float,
    val tempRiseC: Float,
    val averageVoltageMv: Double,
    val averageCurrentUa: Double?,
    val cycleContribution: Double
)

data class SessionWithMetrics(
    val session: ChargingSessionEntity,
    val metrics: SessionMetrics,
    val samples: List<BatterySampleEntity>
)
