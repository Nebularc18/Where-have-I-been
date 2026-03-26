package com.hampu.wherehaveibeen.data.settings

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class OfflineFirstAppSettingsRepository(
    private val dataStore: DataStore<Preferences>
) : AppSettingsRepository {
    override fun observeSettings(): Flow<AppSettings> {
        return dataStore.data.map { preferences ->
            AppSettings(
                useDarkTheme = preferences[UseDarkThemeKey] ?: true,
                accentColor = Color(preferences[AccentColorKey] ?: DefaultAccentColor.toArgb()),
                visitedColor = Color(preferences[VisitedColorKey] ?: DefaultVisitedColor.toArgb()),
                wishlistColor = Color(preferences[WishlistColorKey] ?: DefaultWishlistColor.toArgb()),
                mapBaseColor = Color(preferences[MapBaseColorKey] ?: DefaultMapBaseColor.toArgb())
            )
        }
    }

    override suspend fun setUseDarkTheme(enabled: Boolean) {
        dataStore.edit { it[UseDarkThemeKey] = enabled }
    }

    override suspend fun setAccentColor(color: Int) {
        dataStore.edit { it[AccentColorKey] = color }
    }

    override suspend fun setVisitedColor(color: Int) {
        dataStore.edit { it[VisitedColorKey] = color }
    }

    override suspend fun setWishlistColor(color: Int) {
        dataStore.edit { it[WishlistColorKey] = color }
    }

    override suspend fun setMapBaseColor(color: Int) {
        dataStore.edit { it[MapBaseColorKey] = color }
    }

    override suspend fun resetColors() {
        dataStore.edit {
            it[AccentColorKey] = DefaultAccentColor.toArgb()
            it[VisitedColorKey] = DefaultVisitedColor.toArgb()
            it[WishlistColorKey] = DefaultWishlistColor.toArgb()
            it[MapBaseColorKey] = DefaultMapBaseColor.toArgb()
        }
    }

    companion object {
        private val UseDarkThemeKey = booleanPreferencesKey("use_dark_theme")
        private val AccentColorKey = intPreferencesKey("accent_color")
        private val VisitedColorKey = intPreferencesKey("visited_color")
        private val WishlistColorKey = intPreferencesKey("wishlist_color")
        private val MapBaseColorKey = intPreferencesKey("map_base_color")

        fun fromContext(context: Context): OfflineFirstAppSettingsRepository {
            return OfflineFirstAppSettingsRepository(context.dataStore)
        }
    }
}
