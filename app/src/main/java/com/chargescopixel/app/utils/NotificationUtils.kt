package com.chargescopixel.app.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.chargescopixel.app.R

object NotificationUtils {
    const val MONITOR_CHANNEL_ID = "charge_scope_monitor"
    const val ALERT_CHANNEL_ID = "charge_scope_alerts"

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val monitorChannel = NotificationChannel(
            MONITOR_CHANNEL_ID,
            "Charging Monitor",
            NotificationManager.IMPORTANCE_LOW
        )

        val alertsChannel = NotificationChannel(
            ALERT_CHANNEL_ID,
            "Charging Alerts",
            NotificationManager.IMPORTANCE_HIGH
        )

        manager.createNotificationChannel(monitorChannel)
        manager.createNotificationChannel(alertsChannel)
    }

    fun buildMonitorNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, MONITOR_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.monitoring_notification_title))
            .setContentText(context.getString(R.string.monitoring_notification_text))
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setOngoing(true)
            .build()
    }

    fun showAlert(context: Context, title: String, message: String, id: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        manager.notify(id, notification)
    }
}
