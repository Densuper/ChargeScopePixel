package com.chargescopixel.app

import android.app.Application
import com.chargescopixel.app.data.local.AppDatabase
import com.chargescopixel.app.data.repository.ChargingRepository
import com.chargescopixel.app.data.repository.SettingsRepository

class ChargeScopeApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}

class AppContainer(application: Application) {
    private val database = AppDatabase.getInstance(application)

    val settingsRepository = SettingsRepository(application)
    val chargingRepository = ChargingRepository(
        context = application,
        database = database,
        settingsRepository = settingsRepository
    )
}
