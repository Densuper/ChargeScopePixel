package com.chargescopixel.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chargescopixel.app.data.repository.ChargingRepository
import com.chargescopixel.app.domain.InsightsSummary
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InsightsViewModel(private val repository: ChargingRepository) : ViewModel() {
    val summary: StateFlow<InsightsSummary> = repository.observeDashboardSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InsightsSummary(0, 0, 0.0, 0.0, 0.0, 0f))

    private val _exportedUri = MutableSharedFlow<Uri>()
    val exportedUri: SharedFlow<Uri> = _exportedUri.asSharedFlow()

    fun exportCsv() {
        viewModelScope.launch {
            val uri = repository.exportCsv()
            _exportedUri.emit(uri)
        }
    }
}
