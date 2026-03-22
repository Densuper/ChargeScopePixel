package com.chargescopixel.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chargescopixel.app.data.repository.ChargingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class BatteryHealthUiState(
    val estimatedCycles: Double = 0.0,
    val averagePeakTemp: Float = 0f,
    val averageChargeSpeed: Double = 0.0,
    val note: String = "Gathering data..."
)

class BatteryHealthViewModel(repository: ChargingRepository) : ViewModel() {
    val uiState: StateFlow<BatteryHealthUiState> = repository.observeSessionsWithMetrics()
        .map { sessions ->
            if (sessions.isEmpty()) {
                BatteryHealthUiState(note = "Complete at least one charging session to estimate health.")
            } else {
                val cycles = sessions.sumOf { it.metrics.cycleContribution }
                val avgPeak = sessions.map { it.metrics.peakTempC }.average().toFloat()
                val avgSpeed = sessions.map { it.metrics.percentPerMinute }.average()
                val note = when {
                    avgPeak >= 43f -> "Battery runs warm often. Consider reducing heat exposure."
                    avgSpeed < 0.2 -> "Charging appears slower than expected. Try another charger/cable."
                    else -> "Battery behavior looks stable."
                }
                BatteryHealthUiState(
                    estimatedCycles = cycles,
                    averagePeakTemp = avgPeak,
                    averageChargeSpeed = avgSpeed,
                    note = note
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BatteryHealthUiState())
}
