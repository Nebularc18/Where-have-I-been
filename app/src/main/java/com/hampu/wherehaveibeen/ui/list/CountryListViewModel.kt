package com.hampu.wherehaveibeen.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hampu.wherehaveibeen.data.repository.CountryRepository
import com.hampu.wherehaveibeen.domain.model.matchesQuery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CountryListViewModel(
    private val repository: CountryRepository
) : ViewModel() {
    private val loading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<CountryListUiState> = combine(
        repository.observeAllCountries(),
        searchQuery,
        loading,
        errorMessage
    ) { countries, query, isLoading, error ->
        CountryListUiState(
            isLoading = isLoading,
            errorMessage = error,
            searchQuery = query,
            countries = countries.filter { it.matchesQuery(query) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CountryListUiState()
    )

    init {
        viewModelScope.launch {
            runCatching { repository.seedIfNeeded() }
                .onFailure { errorMessage.value = it.message ?: "Failed to load countries." }
            loading.value = false
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun setVisited(isoCode: String, visited: Boolean) {
        viewModelScope.launch {
            repository.setVisited(isoCode, visited)
        }
    }

    fun setWishlisted(isoCode: String, wishlisted: Boolean) {
        viewModelScope.launch {
            repository.setWishlisted(isoCode, wishlisted)
        }
    }
}
