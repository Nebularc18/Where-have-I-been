package com.hampu.wherehaveibeen.ui.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hampu.wherehaveibeen.data.settings.AppSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: AppSettingsRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = repository.observeSettings()
        .map { settings -> SettingsUiState(settings = settings) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )

    fun setUseDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            repository.setUseDarkTheme(enabled)
        }
    }

    fun setAccentColor(color: Color) {
        viewModelScope.launch {
            repository.setAccentColor(color.toArgb())
        }
    }

    fun setVisitedColor(color: Color) {
        viewModelScope.launch {
            repository.setVisitedColor(color.toArgb())
        }
    }

    fun setWishlistColor(color: Color) {
        viewModelScope.launch {
            repository.setWishlistColor(color.toArgb())
        }
    }

    fun setMapBaseColor(color: Color) {
        viewModelScope.launch {
            repository.setMapBaseColor(color.toArgb())
        }
    }

    fun resetColors() {
        viewModelScope.launch {
            repository.resetColors()
        }
    }
}
