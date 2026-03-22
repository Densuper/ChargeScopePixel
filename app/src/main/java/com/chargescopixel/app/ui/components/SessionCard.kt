package com.chargescopixel.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Started ${FormatUtils.formatDate(session.startTimeMs)}",
            style = MaterialTheme.typography.titleMedium
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("+${metrics.batteryGained}%")
            Text("${"%.2f".format(metrics.percentPerMinute)}%/min")
            Text("Peak ${"%.1f".format(metrics.peakTempC)}°C")
        }
        Text(
            text = "Duration ${FormatUtils.formatDuration(metrics.durationMs)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
