package com.hampu.wherehaveibeen.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {
    @Query("SELECT * FROM countries ORDER BY name ASC")
    fun observeAllCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE isVisited = 1 ORDER BY name ASC")
    fun observeVisitedCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE isWishlisted = 1 ORDER BY name ASC")
    fun observeWishlistedCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE isoCode = :isoCode LIMIT 1")
    suspend fun getCountryByIsoCode(isoCode: String): CountryEntity?

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun countCountries(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<CountryEntity>)

    @Query("UPDATE countries SET isVisited = :visited WHERE isoCode = :isoCode")
    suspend fun setVisited(isoCode: String, visited: Boolean)

    @Query("UPDATE countries SET isWishlisted = :wishlisted WHERE isoCode = :isoCode")
    suspend fun setWishlisted(isoCode: String, wishlisted: Boolean)
}
