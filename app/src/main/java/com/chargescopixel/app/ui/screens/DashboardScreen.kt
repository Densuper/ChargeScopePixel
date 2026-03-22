package com.chargescopixel.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chargescopixel.app.domain.InsightsSummary
import com.chargescopixel.app.domain.SessionWithMetrics
import com.chargescopixel.app.ui.components.SessionCard
import com.chargescopixel.app.ui.components.StatCard

@Composable
fun DashboardScreen(summary: InsightsSummary, latestSession: SessionWithMetrics?) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("ChargeScope Pixel", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            StatCard("Total Sessions", summary.totalSessions.toString(), modifier = Modifier.fillMaxWidth())
        }
        item {
            StatCard("Total Charge Gained", "${summary.totalChargePercentGained}%", modifier = Modifier.fillMaxWidth())
        }
        item {
            StatCard("Estimated Full Cycles", "${"%.2f".format(summary.estimatedCycles)}", modifier = Modifier.fillMaxWidth())
        }
        latestSession?.let {
            item {
                Text("Latest Session", style = MaterialTheme.typography.titleLarge)
            }
            item {
                SessionCard(it)
            }
        }
    }
}
