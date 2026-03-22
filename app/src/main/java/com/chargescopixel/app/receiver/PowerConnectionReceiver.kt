package com.chargescopixel.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chargescopixel.app.service.ChargingMonitorService

class PowerConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_POWER_CONNECTED -> ChargingMonitorService.start(context)
            Intent.ACTION_POWER_DISCONNECTED -> ChargingMonitorService.stop(context)
        }
    }
}
