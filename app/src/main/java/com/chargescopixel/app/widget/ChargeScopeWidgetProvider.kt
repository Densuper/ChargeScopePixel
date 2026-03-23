package com.chargescopixel.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent

class ChargeScopeWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        ChargeScopeWidgetUpdater.refreshAll(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ChargeScopeWidgetUpdater.ACTION_REFRESH_WIDGET ||
            intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE
        ) {
            ChargeScopeWidgetUpdater.refreshAll(context)
        }
    }
}
