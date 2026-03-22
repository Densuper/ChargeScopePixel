package com.chargescopixel.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chargescopixel.app.data.repository.ChargingRepository
import com.chargescopixel.app.domain.InsightsSummary
import com.chargescopixel.app.domain.SessionWithMetrics
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(repository: ChargingRepository) : ViewModel() {
    val summary: StateFlow<InsightsSummary> = repository.observeDashboardSummary()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InsightsSummary(0, 0, 0.0, 0.0, 0.0, 0f)
        )

    val latestSession: StateFlow<SessionWithMetrics?> = repository.observeSessionsWithMetrics()
        .map { sessions -> sessions.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
