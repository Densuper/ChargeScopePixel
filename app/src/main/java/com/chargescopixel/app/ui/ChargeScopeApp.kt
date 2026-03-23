package com.chargescopixel.app.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chargescopixel.app.AppContainer
import com.chargescopixel.app.ui.screens.BatteryHealthScreen
import com.chargescopixel.app.ui.screens.DashboardScreen
import com.chargescopixel.app.ui.screens.InsightsScreen
import com.chargescopixel.app.ui.screens.LiveMonitorScreen
import com.chargescopixel.app.ui.screens.SessionsScreen
import com.chargescopixel.app.ui.screens.SettingsScreen
import com.chargescopixel.app.viewmodel.AppViewModelFactory
import com.chargescopixel.app.viewmodel.BatteryHealthViewModel
import com.chargescopixel.app.viewmodel.DashboardViewModel
import com.chargescopixel.app.viewmodel.InsightsViewModel
import com.chargescopixel.app.viewmodel.LiveMonitorViewModel
import com.chargescopixel.app.viewmodel.SessionsViewModel
import com.chargescopixel.app.viewmodel.SettingsViewModel

private data class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun ChargeScopeApp(appContainer: AppContainer) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val factory = remember(appContainer) { AppViewModelFactory(appContainer) }

    val destinations = listOf(
        AppDestination("dashboard", "Dashboard", Icons.Default.Home),
        AppDestination("live", "Live", Icons.Default.Bolt),
        AppDestination("sessions", "Sessions", Icons.Default.Timeline),
        AppDestination("health", "Health", Icons.Default.BatteryChargingFull),
        AppDestination("insights", "Insights", Icons.Default.Analytics),
        AppDestination("settings", "Settings", Icons.Default.Settings)
    )

    val dashboardVm: DashboardViewModel = viewModel(factory = factory)
    val liveVm: LiveMonitorViewModel = viewModel(factory = factory)
    val sessionsVm: SessionsViewModel = viewModel(factory = factory)
    val healthVm: BatteryHealthViewModel = viewModel(factory = factory)
    val insightsVm: InsightsViewModel = viewModel(factory = factory)
    val settingsVm: SettingsViewModel = viewModel(factory = factory)

    LaunchedEffect(Unit) {
        insightsVm.exportedUri.collect { uri ->
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(sendIntent, "Export ChargeScope CSV")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(context, chooser, null)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            Surface(
                tonalElevation = 10.dp,
                shadowElevation = 18.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    destinations.forEach { destination ->
                        val selected = currentRoute == destination.route
                        val container = if (selected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
                        }
                        val content = if (selected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Surface(
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            color = container,
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
                                            else Color.Transparent
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.label,
                                        tint = content
                                    )
                                }

                                Text(
                                    text = destination.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = content
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    )
                )
        ) {
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("dashboard") {
                    val summary by dashboardVm.summary.collectAsState()
                    val latest by dashboardVm.latestSession.collectAsState()
                    DashboardScreen(summary, latest)
                }
                composable("live") {
                    val samples by liveVm.samples.collectAsState()
                    LiveMonitorScreen(samples)
                }
                composable("sessions") {
                    val sessions by sessionsVm.sessions.collectAsState()
                    SessionsScreen(sessions)
                }
                composable("health") {
                    val state by healthVm.uiState.collectAsState()
                    BatteryHealthScreen(state)
                }
                composable("insights") {
                    val summary by insightsVm.summary.collectAsState()
                    InsightsScreen(summary = summary, onExportCsv = insightsVm::exportCsv)
                }
                composable("settings") {
                    val settings by settingsVm.settings.collectAsState()
                    SettingsScreen(
                        settings = settings,
                        onAlertsChanged = settingsVm::setAlertsEnabled,
                        onOverheatChanged = settingsVm::setOverheatThreshold,
                        onSlowThresholdChanged = settingsVm::setSlowChargingThreshold,
                        onDynamicColorChanged = settingsVm::setDynamicColor
                    )
                }
            }
        }
    }
}
