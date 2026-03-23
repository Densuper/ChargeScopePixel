package com.chargescopixel.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chargescopixel.app.R
import com.chargescopixel.app.data.local.BatterySampleEntity
import com.chargescopixel.app.ui.components.LineChart
import com.chargescopixel.app.ui.components.StatCard

@Composable
fun LiveMonitorScreen(samples: List<BatterySampleEntity>) {
    val latest = samples.lastOrNull()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Live Monitor", style = MaterialTheme.typography.headlineMedium) }

        if (latest != null) {
            item {
                StatCard(
                    title = "Battery",
                    value = "${latest.batteryPercent}%",
                    subtitle = "${latest.chargingStatus} • ${latest.plugType} • ${latest.health} Health",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                StatCard(
                    title = "Technology",
                    value = latest.technology,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                StatCard(
                    title = "Cycle Count",
                    value = latest.cycleCount?.toString() ?: stringResource(id = R.string.not_supported),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text("Battery %", style = MaterialTheme.typography.titleMedium)
                LineChart(points = samples.map { it.batteryPercent.toFloat() }, color = Color(0xFF8FD3FF))
            }
            item {
                Text("Temperature (°C)", style = MaterialTheme.typography.titleMedium)
                LineChart(points = samples.map { it.temperatureC }, color = Color(0xFFFFA57A))
            }
            item {
                Text("Voltage (mV)", style = MaterialTheme.typography.titleMedium)
                LineChart(points = samples.map { it.voltageMv.toFloat() }, color = Color(0xFF9CF7D5))
            }
            item {
                Text("Current (uA)", style = MaterialTheme.typography.titleMedium)
                val currentSeries = samples.mapNotNull { it.currentNowUa?.toFloat() }
                if (currentSeries.isEmpty()) {
                    Text(stringResource(id = R.string.not_supported))
                } else {
                    LineChart(points = currentSeries, color = Color(0xFFB5B2FF))
                }
            }
            item {
                val counterText = latest.chargeCounterUah?.toString() ?: stringResource(id = R.string.not_supported)
                StatCard("Charge Counter (uAh)", counterText, modifier = Modifier.fillMaxWidth())
            }
        } else {
            item {
                Text("Connect charger to start a live session.")
            }
        }
    }
}
