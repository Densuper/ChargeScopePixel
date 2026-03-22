package com.chargescopixel.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chargescopixel.app.data.repository.ChargingRepository
import com.chargescopixel.app.domain.SessionWithMetrics
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SessionsViewModel(repository: ChargingRepository) : ViewModel() {
    val sessions: StateFlow<List<SessionWithMetrics>> = repository.observeSessionsWithMetrics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
