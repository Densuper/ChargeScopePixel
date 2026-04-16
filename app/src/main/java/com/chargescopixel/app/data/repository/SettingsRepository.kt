package com.chargescopixel.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "charge_scope_settings")

data class AppSettings(
    val alertsEnabled: Boolean = true,
    val overheatThresholdC: Float = 42f,
    val slowChargeThresholdPercentPerMinute: Float = 0.15f,
    val dynamicColorEnabled: Boolean = true
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val alertsEnabled = booleanPreferencesKey("alerts_enabled")
        val overheatThresholdC = floatPreferencesKey("overheat_threshold_c")
        val slowChargeThreshold = floatPreferencesKey("slow_charge_threshold")
        val dynamicColor = booleanPreferencesKey("dynamic_color_enabled")
    }

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            alertsEnabled = prefs[Keys.alertsEnabled] ?: true,
            overheatThresholdC = prefs[Keys.overheatThresholdC] ?: 42f,
            slowChargeThresholdPercentPerMinute = prefs[Keys.slowChargeThreshold] ?: 0.15f,
            dynamicColorEnabled = prefs[Keys.dynamicColor] ?: true
        )
    }

    suspend fun setAlertsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.alertsEnabled] = enabled }
    }

    suspend fun setOverheatThreshold(value: Float) {
        context.settingsDataStore.edit { it[Keys.overheatThresholdC] = value }
    }

    suspend fun setSlowChargingThreshold(value: Float) {
        context.settingsDataStore.edit { it[Keys.slowChargeThreshold] = value }
    }

    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.dynamicColor] = enabled }
    }
}
