package com.hampu.wherehaveibeen.data.repository

import com.hampu.wherehaveibeen.domain.model.Country
import com.hampu.wherehaveibeen.domain.model.TravelStats
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    fun observeAllCountries(): Flow<List<Country>>
    fun observeVisitedCountries(): Flow<List<Country>>
    fun observeWishlistedCountries(): Flow<List<Country>>
    fun observeStats(): Flow<TravelStats>
    suspend fun toggleVisited(isoCode: String)
    suspend fun toggleWishlisted(isoCode: String)
    suspend fun setVisited(isoCode: String, visited: Boolean)
    suspend fun setWishlisted(isoCode: String, wishlisted: Boolean)
    suspend fun seedIfNeeded()
}
