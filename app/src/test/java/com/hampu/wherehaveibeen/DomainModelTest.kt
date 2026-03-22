package com.hampu.wherehaveibeen

import com.google.common.truth.Truth.assertThat
import com.hampu.wherehaveibeen.data.repository.normalizeCountryCode
import com.hampu.wherehaveibeen.domain.model.Country
import com.hampu.wherehaveibeen.domain.model.matchesQuery
import com.hampu.wherehaveibeen.domain.model.toTravelStats
import org.junit.Test

class DomainModelTest {
    private val countries = listOf(
        Country("SE", "Sweden", "Europe", "🇸🇪", "Stockholm", isVisited = true, isWishlisted = false),
        Country("NO", "Norway", "Europe", "🇳🇴", "Oslo", isVisited = false, isWishlisted = true),
        Country("JP", "Japan", "Asia", "🇯🇵", "Tokyo", isVisited = true, isWishlisted = true)
    )

    @Test
    fun toTravelStats_aggregatesOverallAndContinentCounts() {
        val stats = countries.toTravelStats()

        assertThat(stats.totalCountries).isEqualTo(3)
        assertThat(stats.visitedCountries).isEqualTo(2)
        assertThat(stats.visitedPercentage).isWithin(0.001f).of(2f / 3f)
        assertThat(stats.continents).hasSize(2)
        assertThat(stats.continents.first { it.continent == "Europe" }.total).isEqualTo(2)
    }

    @Test
    fun matchesQuery_checksNameIsoCapitalAndContinent() {
        val sweden = countries.first()

        assertThat(sweden.matchesQuery("swe")).isTrue()
        assertThat(sweden.matchesQuery("SE")).isTrue()
        assertThat(sweden.matchesQuery("stock")).isTrue()
        assertThat(sweden.matchesQuery("europe")).isTrue()
        assertThat(sweden.matchesQuery("tokyo")).isFalse()
    }

    @Test
    fun normalizeCountryCode_trimsAndUppercases() {
        assertThat(normalizeCountryCode(" se ")).isEqualTo("SE")
    }
}
