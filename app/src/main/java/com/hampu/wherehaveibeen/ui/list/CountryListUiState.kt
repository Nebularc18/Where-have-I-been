package com.hampu.wherehaveibeen.ui.list

import com.hampu.wherehaveibeen.domain.model.Country

data class CountryListUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val countries: List<Country> = emptyList()
)
