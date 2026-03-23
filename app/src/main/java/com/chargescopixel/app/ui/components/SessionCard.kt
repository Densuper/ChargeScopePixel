package com.chargescopixel.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chargescopixel.app.domain.SessionWithMetrics
import com.chargescopixel.app.utils.FormatUtils

@Composable
fun SessionCard(sessionWithMetrics: SessionWithMetrics, modifier: Modifier = Modifier) {
    val session = sessionWithMetrics.session
    val metrics = sessionWithMetrics.metrics

    AppCardContainer(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Session #${session.id}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Started ${FormatUtils.formatDate(session.startTimeMs)}",
                style = MaterialTheme.typography.titleMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AssistChip(
                    onClick = {},
                    label = { Text("+${metrics.batteryGained}%") },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                )
                AssistChip(
                    onClick = {},
                    label = { Text("${"%.2f".format(metrics.percentPerMinute)}%/min") },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                )
                AssistChip(
                    onClick = {},
                    label = { Text("Peak ${"%.1f".format(metrics.peakTempC)}°C") },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Duration ${FormatUtils.formatDuration(metrics.durationMs)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
