package com.chargescopixel.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chargescopixel.app.data.repository.AppSettings
import com.chargescopixel.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun setAlertsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAlertsEnabled(enabled) }
    }

    fun setOverheatThreshold(value: Float) {
        viewModelScope.launch { settingsRepository.setOverheatThreshold(value) }
    }

    fun setSlowChargingThreshold(value: Float) {
        viewModelScope.launch { settingsRepository.setSlowChargingThreshold(value) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDynamicColorEnabled(enabled) }
    }
}
