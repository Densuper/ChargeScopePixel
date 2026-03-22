package com.chargescopixel.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "battery_samples",
    foreignKeys = [
        ForeignKey(
            entity = ChargingSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("timestampMs")]
)
data class BatterySampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val timestampMs: Long,
    val batteryPercent: Int,
    val chargingStatus: String,
    val plugType: String,
    val temperatureC: Float,
    val voltageMv: Int,
    val currentNowUa: Int?,
    val chargeCounterUah: Int?,
    val thermalStatus: Int?
)
