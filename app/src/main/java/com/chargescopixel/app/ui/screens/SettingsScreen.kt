package com.chargescopixel.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chargescopixel.app.data.repository.AppSettings
import com.chargescopixel.app.ui.components.AppCardContainer

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onAlertsChanged: (Boolean) -> Unit,
    onOverheatChanged: (Float) -> Unit,
    onSlowThresholdChanged: (Float) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Settings", style = MaterialTheme.typography.headlineMedium) }

        item {
            AppCardContainer(modifier = Modifier.fillMaxWidth()) {
                SettingRow(
                    title = "Alerts",
                    subtitle = "Enable overheating and slow-charging alerts",
                    checked = settings.alertsEnabled,
                    onCheckedChange = onAlertsChanged
                )
            }
        }

        item {
            AppCardContainer(modifier = Modifier.fillMaxWidth()) {
                SettingRow(
                    title = "Dynamic color",
                    subtitle = "Use Material You colors on supported Pixel phones",
                    checked = settings.dynamicColorEnabled,
                    onCheckedChange = onDynamicColorChanged
                )
            }
        }

        item {
            AppCardContainer(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Overheat Threshold",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${"%.1f".format(settings.overheatThresholdC)}°C",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
                )
                Slider(
                    value = settings.overheatThresholdC,
                    onValueChange = onOverheatChanged,
                    valueRange = 35f..50f
                )
            }
        }

        item {
            AppCardContainer(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Slow Charging Threshold",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${"%.2f".format(settings.slowChargeThresholdPercentPerMinute)}%/min",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
                )
                Slider(
                    value = settings.slowChargeThresholdPercentPerMinute,
                    onValueChange = onSlowThresholdChanged,
                    valueRange = 0.05f..1.5f
                )
            }
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
