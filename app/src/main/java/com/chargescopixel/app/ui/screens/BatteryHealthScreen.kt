package com.chargescopixel.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chargescopixel.app.ui.components.StatCard
import com.chargescopixel.app.viewmodel.BatteryHealthUiState

@Composable
fun BatteryHealthScreen(state: BatteryHealthUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Battery Health", style = MaterialTheme.typography.headlineMedium) }
        item { StatCard("Estimated Full Cycles", "${"%.2f".format(state.estimatedCycles)}", modifier = Modifier.fillMaxWidth()) }
        item { StatCard("Average Peak Temp", "${"%.1f".format(state.averagePeakTemp)}°C", modifier = Modifier.fillMaxWidth()) }
        item { StatCard("Average Charge Speed", "${"%.2f".format(state.averageChargeSpeed)}%/min", modifier = Modifier.fillMaxWidth()) }
        item { Text(state.note) }
    }
}
