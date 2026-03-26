package com.hampu.wherehaveibeen.ui.wishlist

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

class WishlistViewModel(
    private val repository: CountryRepository
) : ViewModel() {
    private val loading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<WishlistUiState> = combine(
        repository.observeWishlistedCountries(),
        searchQuery,
        loading,
        errorMessage
    ) { countries, query, isLoading, error ->
        WishlistUiState(
            isLoading = isLoading,
            errorMessage = error,
            searchQuery = query,
            countries = countries.filter { it.matchesQuery(query) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WishlistUiState()
    )

    init {
        viewModelScope.launch {
            runCatching { repository.seedIfNeeded() }
                .onFailure { errorMessage.value = it.message ?: "Failed to load wishlist." }
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
