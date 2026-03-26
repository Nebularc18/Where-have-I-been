package com.hampu.wherehaveibeen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hampu.wherehaveibeen.data.repository.CountryRepository
import com.hampu.wherehaveibeen.data.settings.AppSettingsRepository
import com.hampu.wherehaveibeen.ui.list.CountryListViewModel
import com.hampu.wherehaveibeen.ui.map.MapViewModel
import com.hampu.wherehaveibeen.ui.settings.SettingsViewModel
import com.hampu.wherehaveibeen.ui.stats.StatsViewModel
import com.hampu.wherehaveibeen.ui.wishlist.WishlistViewModel

class AppViewModelFactory(
    private val repository: CountryRepository,
    private val appSettingsRepository: AppSettingsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MapViewModel::class.java) ->
                MapViewModel(repository) as T
            modelClass.isAssignableFrom(CountryListViewModel::class.java) ->
                CountryListViewModel(repository) as T
            modelClass.isAssignableFrom(StatsViewModel::class.java) ->
                StatsViewModel(repository) as T
            modelClass.isAssignableFrom(WishlistViewModel::class.java) ->
                WishlistViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(appSettingsRepository) as T
            else -> error("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
