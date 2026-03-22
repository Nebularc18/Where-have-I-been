package com.hampu.wherehaveibeen.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hampu.wherehaveibeen.data.repository.CountryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapViewModel(
    private val repository: CountryRepository
) : ViewModel() {
    private val loading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<MapUiState> = combine(
        repository.observeAllCountries(),
        repository.observeStats(),
        loading,
        errorMessage
    ) { countries, stats, isLoading, error ->
        MapUiState(
            isLoading = isLoading,
            errorMessage = error,
            countries = countries,
            stats = stats
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MapUiState()
    )

    init {
        viewModelScope.launch {
            runCatching { repository.seedIfNeeded() }
                .onFailure { errorMessage.value = it.message ?: "Failed to load countries." }
            loading.value = false
        }
    }

    fun toggleVisited(isoCode: String) {
        viewModelScope.launch {
            repository.toggleVisited(isoCode)
        }
    }
}
