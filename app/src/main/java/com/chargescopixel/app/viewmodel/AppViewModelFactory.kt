package com.chargescopixel.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chargescopixel.app.AppContainer

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(container.chargingRepository) as T
            modelClass.isAssignableFrom(LiveMonitorViewModel::class.java) -> LiveMonitorViewModel(container.chargingRepository) as T
            modelClass.isAssignableFrom(SessionsViewModel::class.java) -> SessionsViewModel(container.chargingRepository) as T
            modelClass.isAssignableFrom(BatteryHealthViewModel::class.java) -> BatteryHealthViewModel(container.chargingRepository) as T
            modelClass.isAssignableFrom(InsightsViewModel::class.java) -> InsightsViewModel(container.chargingRepository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(container.settingsRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
