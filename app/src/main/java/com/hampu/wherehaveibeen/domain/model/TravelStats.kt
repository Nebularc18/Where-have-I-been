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
    val totalContinents: Int,
    val visitedContinents: Int,
    val visitedContinentsPercentage: Float,
    val continents: List<ContinentProgress>
) {
    companion object {
        val Empty = TravelStats(
            totalCountries = 0,
            visitedCountries = 0,
            visitedPercentage = 0f,
            totalContinents = 0,
            visitedContinents = 0,
            visitedContinentsPercentage = 0f,
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
                percentage = ratioOrZero(visited, countries.size)
            )
        }

    val visitedContinents = totalsByContinent.count { it.visited > 0 }
    val totalContinents = totalsByContinent.size

    return TravelStats(
        totalCountries = size,
        visitedCountries = visitedCountries,
        visitedPercentage = ratioOrZero(visitedCountries, size),
        totalContinents = totalContinents,
        visitedContinents = visitedContinents,
        visitedContinentsPercentage = ratioOrZero(visitedContinents, totalContinents),
        continents = totalsByContinent
    )
}

private fun ratioOrZero(numerator: Int, denominator: Int): Float {
    if (denominator <= 0) return 0f
    return numerator.toFloat() / denominator.toFloat()
}
