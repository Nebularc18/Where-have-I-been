package com.hampu.wherehaveibeen.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hampu.wherehaveibeen.data.repository.CountryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StatsViewModel(
    private val repository: CountryRepository
) : ViewModel() {
    private val loading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<StatsUiState> = combine(
        repository.observeStats(),
        loading,
        errorMessage
    ) { stats, isLoading, error ->
        StatsUiState(
            isLoading = isLoading,
            errorMessage = error,
            stats = stats
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatsUiState()
    )

    init {
        viewModelScope.launch {
            runCatching { repository.seedIfNeeded() }
                .onFailure { errorMessage.value = it.message ?: "Failed to calculate stats." }
            loading.value = false
        }
    }
}
