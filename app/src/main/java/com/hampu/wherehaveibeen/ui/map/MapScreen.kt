package com.hampu.wherehaveibeen.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eltonkola.bota.WorldMap
import com.hampu.wherehaveibeen.data.repository.normalizeCountryCode
import com.hampu.wherehaveibeen.ui.components.EmptyContent
import com.hampu.wherehaveibeen.ui.components.ScreenHeader

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MapScreen(
        uiState = uiState,
        onToggleVisited = viewModel::toggleVisited,
        modifier = modifier
    )
}

@Composable
fun MapScreen(
    uiState: MapUiState,
    onToggleVisited: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
            subtitle = "${(uiState.stats.visitedPercentage * 100).toInt()}% of your curated world map"
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
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            WorldMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .semantics { testTag = "worldMap" },
                highlightedCountryIds = uiState.countries
                    .filter { it.isVisited }
                    .map { it.isoCode }
                    .toSet(),
                defaultColor = Color(0xFFE6ECEF),
                highlightColor = MaterialTheme.colorScheme.primary,
                strokeColor = Color.White,
                onCountryClick = { country ->
                    onToggleVisited(normalizeCountryCode(country.id))
                }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendChip(
                color = Color(0xFFE6ECEF),
                label = "Not visited"
            )
            LegendChip(
                color = MaterialTheme.colorScheme.primary,
                label = "Visited"
            )
        }
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
