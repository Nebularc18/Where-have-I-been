package com.hampu.wherehaveibeen.ui.map

import com.hampu.wherehaveibeen.domain.model.Country
import com.hampu.wherehaveibeen.domain.model.TravelStats

data class MapUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val countries: List<Country> = emptyList(),
    val stats: TravelStats = TravelStats.Empty
)
