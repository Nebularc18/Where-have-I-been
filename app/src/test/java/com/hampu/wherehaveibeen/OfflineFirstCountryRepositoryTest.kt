package com.hampu.wherehaveibeen

import android.content.ContextWrapper
import com.google.common.truth.Truth.assertThat
import com.hampu.wherehaveibeen.data.local.CountryAssetDataSource
import com.hampu.wherehaveibeen.data.local.CountryDao
import com.hampu.wherehaveibeen.data.local.CountryEntity
import com.hampu.wherehaveibeen.data.local.CountrySeed
import com.hampu.wherehaveibeen.data.repository.OfflineFirstCountryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test

class OfflineFirstCountryRepositoryTest {
    @Test
    fun seedIfNeeded_populatesDaoAndToggleMethodsUpdateFlags() = runBlocking {
        val dao = FakeCountryDao()
        val repository = OfflineFirstCountryRepository(
            countryDao = dao,
            assetDataSource = fakeAssetDataSource(
                listOf(
                    CountrySeed("SE", "Sweden", "Europe", "🇸🇪", "Stockholm"),
                    CountrySeed("JP", "Japan", "Asia", "🇯🇵", "Tokyo")
                )
            ),
            ioDispatcher = Dispatchers.Unconfined
        )

        repository.seedIfNeeded()
        repository.toggleVisited("se")
        repository.toggleWishlisted("JP")

        val countries = repository.observeAllCountries().first()

        assertThat(countries).hasSize(2)
        assertThat(countries.first { it.isoCode == "SE" }.isVisited).isTrue()
        assertThat(countries.first { it.isoCode == "JP" }.isWishlisted).isTrue()
    }

    private fun fakeAssetDataSource(countries: List<CountrySeed>): CountryAssetDataSource {
        return object : CountryAssetDataSource(object : ContextWrapper(null) {}) {
            override fun loadCountries(): List<CountrySeed> = countries
        }
    }
}

private class FakeCountryDao : CountryDao {
    private val state = MutableStateFlow<List<CountryEntity>>(emptyList())

    override fun observeAllCountries(): Flow<List<CountryEntity>> = state

    override fun observeVisitedCountries(): Flow<List<CountryEntity>> =
        state.map { countries -> countries.filter { it.isVisited } }

    override fun observeWishlistedCountries(): Flow<List<CountryEntity>> =
        state.map { countries -> countries.filter { it.isWishlisted } }

    override suspend fun getCountryByIsoCode(isoCode: String): CountryEntity? =
        state.value.firstOrNull { it.isoCode == isoCode }

    override suspend fun countCountries(): Int = state.value.size

    override suspend fun insertAll(countries: List<CountryEntity>) {
        state.value = countries.sortedBy { it.name }
    }

    override suspend fun setVisited(isoCode: String, visited: Boolean) {
        state.value = state.value.map { country ->
            if (country.isoCode == isoCode) country.copy(isVisited = visited) else country
        }
    }

    override suspend fun setWishlisted(isoCode: String, wishlisted: Boolean) {
        state.value = state.value.map { country ->
            if (country.isoCode == isoCode) country.copy(isWishlisted = wishlisted) else country
        }
    }
}
