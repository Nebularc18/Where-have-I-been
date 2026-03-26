package com.hampu.wherehaveibeen.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hampu.wherehaveibeen.ui.components.CountryRow
import com.hampu.wherehaveibeen.ui.components.EmptyContent
import com.hampu.wherehaveibeen.ui.components.ScreenHeader

@Composable
fun CountryListScreen(
    viewModel: CountryListViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CountryListScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onVisitedChange = viewModel::setVisited,
        onWishlistChange = viewModel::setWishlisted,
        modifier = modifier
    )
}

@Composable
fun CountryListScreen(
    uiState: CountryListUiState,
    onSearchQueryChange: (String) -> Unit,
    onVisitedChange: (String, Boolean) -> Unit,
    onWishlistChange: (String, Boolean) -> Unit,
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
            title = "All countries",
            subtitle = "Search and mark places as visited or saved for later."
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

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { testTag = "countrySearch" },
            label = { Text("Search countries") },
            singleLine = true
        )

        if (uiState.countries.isEmpty()) {
            EmptyContent(
                title = "No matching countries",
                message = "Try a different country, capital, ISO code, or continent."
            )
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = uiState.countries,
                key = { it.isoCode }
            ) { country ->
                CountryRow(
                    country = country,
                    onVisitedChange = onVisitedChange,
                    onWishlistChange = onWishlistChange
                )
            }
        }
    }
}
