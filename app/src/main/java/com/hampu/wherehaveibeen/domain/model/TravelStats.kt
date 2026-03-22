package com.hampu.wherehaveibeen.domain.model

data class ContinentProgress(
    val continent: String,
    val visited: Int,
    val total: Int,
    val percentage: Float
)

data class TravelStats(
    val totalCountries: Int,
    val visitedCountries: Int,
    val visitedPercentage: Float,
    val continents: List<ContinentProgress>
) {
    companion object {
        val Empty = TravelStats(
            totalCountries = 0,
            visitedCountries = 0,
            visitedPercentage = 0f,
            continents = emptyList()
        )
    }
}

fun List<Country>.toTravelStats(): TravelStats {
    if (isEmpty()) return TravelStats.Empty

    val visitedCountries = count { it.isVisited }
    val totalsByContinent = groupBy(Country::continent)
        .toSortedMap()
        .map { (continent, countries) ->
            val visited = countries.count { it.isVisited }
            ContinentProgress(
                continent = continent,
                visited = visited,
                total = countries.size,
                percentage = visited.toFloat() / countries.size.toFloat()
            )
        }

    return TravelStats(
        totalCountries = size,
        visitedCountries = visitedCountries,
        visitedPercentage = visitedCountries.toFloat() / size.toFloat(),
        continents = totalsByContinent
    )
}
