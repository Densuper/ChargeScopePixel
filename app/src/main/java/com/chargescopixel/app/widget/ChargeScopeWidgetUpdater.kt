package com.chargescopixel.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.chargescopixel.app.MainActivity
import com.chargescopixel.app.R
import com.chargescopixel.app.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

object ChargeScopeWidgetUpdater {
    const val ACTION_REFRESH_WIDGET = "com.chargescopixel.app.widget.REFRESH"

    fun requestRefresh(context: Context) {
        context.sendBroadcast(Intent(context, ChargeScopeWidgetProvider::class.java).apply {
            action = ACTION_REFRESH_WIDGET
        })
    }

    fun refreshAll(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val provider = ComponentName(context, ChargeScopeWidgetProvider::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(provider)
            if (widgetIds.isEmpty()) return@launch

            val db = AppDatabase.getInstance(context)
            val latest = db.batterySampleDao().getLatestSamplesOnce(24).reversed()

            widgetIds.forEach { widgetId ->
                val views = RemoteViews(context.packageName, R.layout.widget_charge_scope)

                val openIntent = Intent(context, MainActivity::class.java)
                val pendingOpen = PendingIntent.getActivity(
                    context,
                    widgetId,
                    openIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingOpen)

                if (latest.isEmpty()) {
                    views.setTextViewText(R.id.widget_status, "Connect charger to start monitoring")
                    views.setTextViewText(R.id.widget_primary, "No data")
                    views.setTextViewText(R.id.widget_secondary, "ChargeScope Pixel")
                    views.setImageViewResource(R.id.widget_graph, android.R.color.transparent)
                } else {
                    val now = latest.last()
                    val batteryPoints = latest.map { it.batteryPercent.toFloat() }
                    val tempPoints = latest.map { it.temperatureC }

                    val trend = if (batteryPoints.size > 1) {
                        val d = batteryPoints.last() - batteryPoints.first()
                        val sign = if (d >= 0f) "+" else "-"
                        "$sign${"%.1f".format(abs(d))}%"
                    } else {
                        "0.0%"
                    }

                    views.setTextViewText(
                        R.id.widget_status,
                        "${now.chargingStatus} • ${now.plugType}"
                    )
                    views.setTextViewText(
                        R.id.widget_primary,
                        "${now.batteryPercent}%  (${trend})"
                    )
                    views.setTextViewText(
                        R.id.widget_secondary,
                        "${"%.1f".format(now.temperatureC)}°C • ${"%.2f".format(now.voltageMv / 1000.0)}V"
                    )

                    val graphBitmap = WidgetSparklineRenderer.render(
                        points = batteryPoints,
                        lineColor = 0xFF8FD3FF.toInt(),
                        gridColor = 0x44FFFFFF
                    )
                    views.setImageViewBitmap(R.id.widget_graph, graphBitmap)

                    val tempBitmap = WidgetSparklineRenderer.render(
                        points = tempPoints,
                        lineColor = 0xFFFFA57A.toInt(),
                        gridColor = 0x33FFFFFF
                    )
                    views.setImageViewBitmap(R.id.widget_graph_temp, tempBitmap)
                }

                appWidgetManager.updateAppWidget(widgetId, views)
            }
        }
    }
}
