package com.hampu.wherehaveibeen

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hampu.wherehaveibeen.data.repository.CountryRepository
import com.hampu.wherehaveibeen.domain.model.Country
import com.hampu.wherehaveibeen.domain.model.TravelStats
import com.hampu.wherehaveibeen.domain.model.toTravelStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val repository = FakeCountryRepository()

    @Test
    fun bottomNavigationAndCountryFlowsWork() {
        composeRule.setContent {
            WhereHaveIBeenApp(repository = repository)
        }

        composeRule.onNodeWithText("List").performClick()
        composeRule.onNodeWithText("All countries").assertExists()
        composeRule.onNodeWithText("Wishlist").performClick()
        composeRule.onNodeWithText("No wishlist countries").assertExists()

        composeRule.onNodeWithText("List").performClick()
        composeRule.onNodeWithTag("countrySearch").performTextInput("Japan")
        composeRule.onNodeWithText("Japan").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Toggle wishlist for Japan").performClick()

        composeRule.onNodeWithText("Wishlist").performClick()
        composeRule.onNodeWithText("Japan").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Toggle visited for Japan").performClick()

        composeRule.onNodeWithText("Stats").performClick()
        composeRule.onNodeWithTag("overallProgress").assertExists()

        composeRule.onNodeWithText("Map").performClick()
        composeRule.onNodeWithTag("worldMap").assertExists()
    }
}

private class FakeCountryRepository : CountryRepository {
    private val countries = MutableStateFlow(
        listOf(
            Country("SE", "Sweden", "Europe", "🇸🇪", "Stockholm", isVisited = false, isWishlisted = false),
            Country("JP", "Japan", "Asia", "🇯🇵", "Tokyo", isVisited = false, isWishlisted = false),
            Country("BR", "Brazil", "South America", "🇧🇷", "Brasília", isVisited = true, isWishlisted = false)
        )
    )

    override fun observeAllCountries(): Flow<List<Country>> = countries

    override fun observeVisitedCountries(): Flow<List<Country>> = countries.map { list ->
        list.filter { it.isVisited }
    }

    override fun observeWishlistedCountries(): Flow<List<Country>> = countries.map { list ->
        list.filter { it.isWishlisted }
    }

    override fun observeStats(): Flow<TravelStats> = countries.map(List<Country>::toTravelStats)

    override suspend fun toggleVisited(isoCode: String) {
        countries.value = countries.value.map { country ->
            if (country.isoCode == isoCode.uppercase()) {
                country.copy(isVisited = !country.isVisited)
            } else {
                country
            }
        }
    }

    override suspend fun toggleWishlisted(isoCode: String) {
        countries.value = countries.value.map { country ->
            if (country.isoCode == isoCode.uppercase()) {
                country.copy(isWishlisted = !country.isWishlisted)
            } else {
                country
            }
        }
    }

    override suspend fun setVisited(isoCode: String, visited: Boolean) {
        countries.value = countries.value.map { country ->
            if (country.isoCode == isoCode.uppercase()) country.copy(isVisited = visited) else country
        }
    }

    override suspend fun setWishlisted(isoCode: String, wishlisted: Boolean) {
        countries.value = countries.value.map { country ->
            if (country.isoCode == isoCode.uppercase()) country.copy(isWishlisted = wishlisted) else country
        }
    }

    override suspend fun seedIfNeeded() = Unit
}
