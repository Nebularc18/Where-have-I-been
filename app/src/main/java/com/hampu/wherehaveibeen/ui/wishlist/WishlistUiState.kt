package com.hampu.wherehaveibeen.ui.wishlist

import com.hampu.wherehaveibeen.domain.model.Country

data class WishlistUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val countries: List<Country> = emptyList()
)
