package com.hampu.wherehaveibeen.data.repository

import com.hampu.wherehaveibeen.data.local.CountryAssetDataSource
import com.hampu.wherehaveibeen.data.local.CountryDao
import com.hampu.wherehaveibeen.data.local.CountryEntity
import com.hampu.wherehaveibeen.domain.model.Country
import com.hampu.wherehaveibeen.domain.model.TravelStats
import com.hampu.wherehaveibeen.domain.model.toCountry
import com.hampu.wherehaveibeen.domain.model.toTravelStats
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class OfflineFirstCountryRepository(
    private val countryDao: CountryDao,
    private val assetDataSource: CountryAssetDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : CountryRepository {
    private val seedMutex = Mutex()

    override fun observeAllCountries(): Flow<List<Country>> {
        return countryDao.observeAllCountries().map { countries -> countries.map(CountryEntity::toCountry) }
    }

    override fun observeVisitedCountries(): Flow<List<Country>> {
        return countryDao.observeVisitedCountries().map { countries -> countries.map(CountryEntity::toCountry) }
    }

    override fun observeWishlistedCountries(): Flow<List<Country>> {
        return countryDao.observeWishlistedCountries().map { countries -> countries.map(CountryEntity::toCountry) }
    }

    override fun observeStats(): Flow<TravelStats> {
        return observeAllCountries().map(List<Country>::toTravelStats)
    }

    override suspend fun setVisited(isoCode: String, visited: Boolean) = withContext(ioDispatcher) {
        val normalized = normalizeCountryCode(isoCode)
        if (countryDao.getCountryByIsoCode(normalized) != null) {
            countryDao.setVisited(normalized, visited)
        }
    }

    override suspend fun setWishlisted(isoCode: String, wishlisted: Boolean) = withContext(ioDispatcher) {
        val normalized = normalizeCountryCode(isoCode)
        if (countryDao.getCountryByIsoCode(normalized) != null) {
            countryDao.setWishlisted(normalized, wishlisted)
        }
    }

    override suspend fun seedIfNeeded() = withContext(ioDispatcher) {
        seedMutex.withLock {
            if (countryDao.countCountries() > 0) return@withLock
            val seededCountries = assetDataSource.loadCountries().map { country ->
                CountryEntity(
                    isoCode = normalizeCountryCode(country.isoCode),
                    name = country.name,
                    continent = country.continent,
                    flagEmoji = country.flagEmoji,
                    capital = country.capital,
                    isVisited = false,
                    isWishlisted = false
                )
            }
            countryDao.insertAll(seededCountries)
        }
    }
}

fun normalizeCountryCode(value: String): String = value.trim().uppercase()
