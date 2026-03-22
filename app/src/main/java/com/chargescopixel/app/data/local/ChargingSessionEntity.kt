package com.chargescopixel.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charging_sessions")
data class ChargingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTimeMs: Long,
    val endTimeMs: Long? = null,
    val startBatteryPercent: Int,
    val endBatteryPercent: Int? = null,
    val startTempC: Float,
    val peakTempC: Float,
    val endTempC: Float? = null
)
