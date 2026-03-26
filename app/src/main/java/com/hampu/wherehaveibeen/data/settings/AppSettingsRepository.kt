package com.hampu.wherehaveibeen.data.settings

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun setUseDarkTheme(enabled: Boolean)
    suspend fun setAccentColor(color: Int)
    suspend fun setVisitedColor(color: Int)
    suspend fun setWishlistColor(color: Int)
    suspend fun setMapBaseColor(color: Int)
    suspend fun resetColors()
}
