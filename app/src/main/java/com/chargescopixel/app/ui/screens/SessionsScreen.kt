package com.chargescopixel.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chargescopixel.app.domain.SessionWithMetrics
import com.chargescopixel.app.ui.components.SessionCard

@Composable
fun SessionsScreen(sessions: List<SessionWithMetrics>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Charging Sessions", style = MaterialTheme.typography.headlineMedium) }

        if (sessions.isEmpty()) {
            item { Text("No sessions logged yet.") }
        } else {
            items(sessions, key = { it.session.id }) { session ->
                SessionCard(session)
            }
        }
    }
}
