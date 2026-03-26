package com.hampu.wherehaveibeen.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hampu.wherehaveibeen.data.repository.normalizeCountryCode
import com.hampu.wherehaveibeen.ui.components.EmptyContent
import com.hampu.wherehaveibeen.ui.components.HeaderHighlight
import com.hampu.wherehaveibeen.ui.components.ScreenHeader
import com.hampu.wherehaveibeen.ui.theme.LocalAppColorTokens

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MapScreen(
        uiState = uiState,
        onSetVisited = viewModel::setVisited,
        onSetWishlisted = viewModel::setWishlisted,
        modifier = modifier
    )
}

@Composable
fun MapScreen(
    uiState: MapUiState,
    onSetVisited: (String, Boolean) -> Unit,
    onSetWishlisted: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColorTokens.current
    val (selectedCountryIso, setSelectedCountryIso) = remember { mutableStateOf<String?>(null) }
    val selectedCountry = uiState.countries.firstOrNull { it.isoCode == selectedCountryIso }

    if (uiState.isLoading) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenHeader(
            title = "Visited ${uiState.stats.visitedCountries} of ${uiState.stats.totalCountries}",
            subtitle = "${(uiState.stats.visitedPercentage * 100).toInt()}% of your curated world map",
            highlights = listOf(
                HeaderHighlight(
                    label = "Countries",
                    value = "${uiState.stats.visitedCountries}/${uiState.stats.totalCountries}"
                ),
                HeaderHighlight(
                    label = "Continents",
                    value = "${uiState.stats.visitedContinents}/${uiState.stats.totalContinents} (${(uiState.stats.visitedContinentsPercentage * 100).toInt()}%)"
                )
            )
        )

        uiState.errorMessage?.let { message ->
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (uiState.countries.isEmpty()) {
            EmptyContent(
                title = "No countries available",
                message = "The local catalog could not be loaded."
            )
            return
        }

        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            InteractiveWorldMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .semantics { testTag = "worldMap" },
                countryColors = uiState.countries.associate { country ->
                    country.isoCode to when {
                        country.isVisited -> appColors.visited
                        country.isWishlisted -> appColors.wishlist
                        else -> appColors.mapBase
                    }
                },
                defaultColor = appColors.mapBase,
                strokeColor = MaterialTheme.colorScheme.background,
                onCountryClick = { country ->
                    setSelectedCountryIso(uiState.countries.firstOrNull {
                        it.isoCode == normalizeCountryCode(country.id)
                    }?.isoCode)
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendChip(color = appColors.mapBase, label = "Not visited")
            LegendChip(color = appColors.visited, label = "Visited")
            LegendChip(color = appColors.wishlist, label = "Wishlist")
        }
    }

    selectedCountry?.let { country ->
        AlertDialog(
            onDismissRequest = { setSelectedCountryIso(null) },
            title = { Text(country.name) },
            text = {
                Text("${country.flagEmoji} ${country.isoCode} - ${country.capital}")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetVisited(country.isoCode, !country.isVisited)
                        setSelectedCountryIso(null)
                    }
                ) {
                    Text(if (country.isVisited) "Remove visited" else "Mark visited")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        onSetWishlisted(country.isoCode, !country.isWishlisted)
                        setSelectedCountryIso(null)
                    }
                ) {
                    Text(if (country.isWishlisted) "Remove wishlist" else "Add wishlist")
                }
            }
        )
    }
}

@Composable
private fun LegendChip(
    color: Color,
    label: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
