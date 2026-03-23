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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chargescopixel.app.R
import com.chargescopixel.app.data.local.BatterySampleEntity
import com.chargescopixel.app.ui.components.MetricChartCard
import com.chargescopixel.app.ui.components.StatCard
import kotlin.math.abs

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
                val points = samples.map { it.batteryPercent.toFloat() }
                MetricChartCard(
                    title = "Battery %",
                    valueText = "${latest.batteryPercent}%",
                    trendText = buildTrend(points, "%"),
                    points = points,
                    lineColor = MaterialTheme.colorScheme.primary
                )
            }
            item {
                val points = samples.map { it.temperatureC }
                MetricChartCard(
                    title = "Temperature",
                    valueText = formatCelsius(latest.temperatureC),
                    trendText = buildTrend(points, "°C"),
                    points = points,
                    lineColor = MaterialTheme.colorScheme.tertiary
                )
            }
            item {
                // Android battery voltage is provided in mV; convert to V for user-facing display.
                val points = samples.map { it.voltageMv / 1000f }
                val volts = latest.voltageMv / 1000.0
                MetricChartCard(
                    title = "Voltage",
                    valueText = "${"%.2f".format(volts)} V",
                    trendText = buildTrend(points, "V"),
                    points = points,
                    lineColor = MaterialTheme.colorScheme.secondary
                )
            }
            item {
                val currentSeries = samples.mapNotNull { it.currentNowUa?.toFloat()?.div(1000f) } // uA -> mA
                if (currentSeries.isEmpty()) {
                    StatCard(
                        title = "Current",
                        value = stringResource(id = R.string.not_supported),
                        subtitle = "Device does not expose BATTERY_PROPERTY_CURRENT_NOW",
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    val currentMa = latest.currentNowUa?.div(1000.0)
                    MetricChartCard(
                        title = "Current",
                        valueText = "${"%.0f".format(currentMa ?: 0.0)} mA",
                        trendText = buildTrend(currentSeries, "mA"),
                        points = currentSeries,
                        lineColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
            item {
                val counterText = latest.chargeCounterUah
                    ?.let { "${"%.0f".format(it / 1000.0)} mAh" } // uAh -> mAh
                    ?: stringResource(id = R.string.not_supported)
                StatCard(
                    title = "Charge Counter",
                    value = counterText,
                    subtitle = "Raw: ${latest.chargeCounterUah ?: "N/A"} uAh",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            item {
                Text("Connect charger to start a live session.")
            }
        }
    }
}

private fun buildTrend(points: List<Float>, unit: String): String? {
    if (points.size < 2) return null
    val delta = points.last() - points.first()
    val sign = if (delta >= 0f) "+" else "-"
    val magnitude = abs(delta)
    return "$sign${"%.2f".format(magnitude)} $unit (window trend)"
}

private fun formatCelsius(value: Float): String = "${"%.1f".format(value)} °C"
