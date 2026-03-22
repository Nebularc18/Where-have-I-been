package com.hampu.wherehaveibeen.ui.stats

import com.hampu.wherehaveibeen.domain.model.TravelStats

data class StatsUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val stats: TravelStats = TravelStats.Empty
)
