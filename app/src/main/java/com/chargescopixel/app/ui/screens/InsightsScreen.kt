package com.chargescopixel.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chargescopixel.app.domain.InsightsSummary
import com.chargescopixel.app.ui.components.StatCard

@Composable
fun InsightsScreen(summary: InsightsSummary, onExportCsv: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Insights", style = MaterialTheme.typography.headlineMedium) }
        item { StatCard("Average Session Gain", "${"%.1f".format(summary.averageSessionGain)}%", modifier = Modifier.fillMaxWidth()) }
        item { StatCard("Average Session Duration", "${"%.1f".format(summary.averageSessionDurationMinutes)} min", modifier = Modifier.fillMaxWidth()) }
        item { StatCard("Hottest Session", "${"%.1f".format(summary.hottestSessionTempC)}°C", modifier = Modifier.fillMaxWidth()) }
        item {
            Button(onClick = onExportCsv, modifier = Modifier.fillMaxWidth()) {
                Text("Export CSV")
            }
        }
    }
}
