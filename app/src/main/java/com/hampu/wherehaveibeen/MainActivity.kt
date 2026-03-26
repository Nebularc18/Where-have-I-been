package com.hampu.wherehaveibeen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hampu.wherehaveibeen.ui.theme.WhereHaveIBeenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as WhereHaveIBeenApplication).container
        setContent {
            val appSettings by container.appSettingsRepository.observeSettings()
                .collectAsStateWithLifecycle(initialValue = com.hampu.wherehaveibeen.data.settings.AppSettings())
            WhereHaveIBeenTheme(settings = appSettings) {
                WhereHaveIBeenApp(
                    repository = container.countryRepository,
                    appSettingsRepository = container.appSettingsRepository
                )
            }
        }
    }
}
