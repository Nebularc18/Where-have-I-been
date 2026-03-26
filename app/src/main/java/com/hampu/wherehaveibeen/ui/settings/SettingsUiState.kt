package com.hampu.wherehaveibeen.ui.settings

import com.hampu.wherehaveibeen.data.settings.AppSettings

data class SettingsUiState(
    val settings: AppSettings = AppSettings()
)
