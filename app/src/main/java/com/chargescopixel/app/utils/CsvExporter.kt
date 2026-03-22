package com.chargescopixel.app.utils

import android.content.Context
import androidx.core.content.FileProvider
import com.chargescopixel.app.data.local.BatterySampleEntity
import com.chargescopixel.app.data.local.ChargingSessionEntity
import java.io.File

object CsvExporter {

    fun exportSessionsAndSamples(
        context: Context,
        sessions: List<ChargingSessionEntity>,
        samples: List<BatterySampleEntity>
    ): android.net.Uri {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportDir, "chargescopixel_export_${System.currentTimeMillis()}.csv")

        file.bufferedWriter().use { writer ->
            writer.appendLine("type,sessionId,timestamp,startTime,endTime,batteryPercent,temperatureC,voltageMv,currentNowUa,chargeCounterUah,plugType,chargingStatus,thermalStatus")

            sessions.forEach { session ->
                writer.appendLine(
                    "session,${session.id},,${session.startTimeMs},${session.endTimeMs ?: ""},${session.startBatteryPercent},${session.startTempC},,,,,,"
                )
            }

            samples.forEach { sample ->
                writer.appendLine(
                    "sample,${sample.sessionId},${sample.timestampMs},,,${sample.batteryPercent},${sample.temperatureC},${sample.voltageMv},${sample.currentNowUa ?: ""},${sample.chargeCounterUah ?: ""},${sample.plugType},${sample.chargingStatus},${sample.thermalStatus ?: ""}"
                )
            }
        }

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
