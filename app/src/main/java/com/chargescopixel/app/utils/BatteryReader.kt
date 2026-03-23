package com.chargescopixel.app.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import com.chargescopixel.app.domain.BatterySnapshot
import com.chargescopixel.app.domain.PlugType

object BatteryReader {
    fun readSnapshot(context: Context): BatterySnapshot? {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            ?: return null

        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        if (level < 0 || scale <= 0) return null

        val percentage = (level * 100f / scale).toInt()
        val status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        val statusText = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
            else -> "Unknown"
        }

        val health = when (batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Unknown"
        }

        val technology = batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"

        val plugType = when (batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
            BatteryManager.BATTERY_PLUGGED_USB -> PlugType.USB
            BatteryManager.BATTERY_PLUGGED_AC -> PlugType.AC
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> PlugType.WIRELESS
            else -> PlugType.UNKNOWN
        }

        val tempDeciC = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        val voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)

        val currentNow = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            .takeIf { it != Int.MIN_VALUE }
        val chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            .takeIf { it != Int.MIN_VALUE }
        val cycleCount = if (Build.VERSION.SDK_INT >= 34) { // Build.VERSION_CODES.UPSIDE_DOWN_CAKE
            // BATTERY_PROPERTY_CYCLE_COUNT is 7
            batteryManager.getIntProperty(7).takeIf { it != -1 }
        } else {
            null
        }

        val thermalStatus = runCatching { powerManager.currentThermalStatus }.getOrNull()

        return BatterySnapshot(
            timestamp = System.currentTimeMillis(),
            batteryPercent = percentage,
            chargingStatus = statusText,
            health = health,
            technology = technology,
            plugType = plugType,
            temperatureC = tempDeciC / 10f,
            voltageMv = voltage,
            currentNowUa = currentNow,
            chargeCounterUah = chargeCounter,
            cycleCount = cycleCount,
            thermalStatus = thermalStatus
        )
    }

    fun isPluggedIn(context: Context): Boolean {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            ?: return false
        val plug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        return plug != 0
    }
}
