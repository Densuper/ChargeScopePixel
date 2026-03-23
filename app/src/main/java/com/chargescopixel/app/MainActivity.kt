package com.chargescopixel.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import com.chargescopixel.app.service.ChargingMonitorService
import com.chargescopixel.app.ui.ChargeScopeApp
import com.chargescopixel.app.data.repository.AppSettings
import com.chargescopixel.app.ui.theme.ChargeScopeTheme

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
        ChargingMonitorService.ensureMonitoringMatchesCurrentPowerState(this)

        setContent {
            val container = (application as ChargeScopeApplication).appContainer
            val settings by container.settingsRepository.settings.collectAsState(initial = AppSettings())
            ChargeScopeTheme(dynamicColor = settings.dynamicColorEnabled) {
                ChargeScopeApp(appContainer = container)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
