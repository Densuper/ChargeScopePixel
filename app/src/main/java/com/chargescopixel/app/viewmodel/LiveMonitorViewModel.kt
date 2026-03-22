package com.chargescopixel.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chargescopixel.app.data.local.BatterySampleEntity
import com.chargescopixel.app.data.repository.ChargingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LiveMonitorViewModel(repository: ChargingRepository) : ViewModel() {
    val samples: StateFlow<List<BatterySampleEntity>> = repository.observeRecentSamples(120)
        .map { it.reversed() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
