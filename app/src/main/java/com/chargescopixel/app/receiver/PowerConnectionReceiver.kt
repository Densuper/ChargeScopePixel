package com.chargescopixel.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.chargescopixel.app.service.ChargingMonitorService

class PowerConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                Log.d(TAG, "Power connected broadcast received; starting monitor service")
                ChargingMonitorService.start(context)
            }

            Intent.ACTION_POWER_DISCONNECTED -> {
                Log.d(TAG, "Power disconnected broadcast received; stopping monitor service")
                ChargingMonitorService.stop(context)
            }
        }
    }

    companion object {
        private const val TAG = "PowerConnectionReceiver"
    }
}
