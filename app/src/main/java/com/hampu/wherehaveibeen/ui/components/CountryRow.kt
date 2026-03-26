package com.hampu.wherehaveibeen.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hampu.wherehaveibeen.domain.model.Country
import com.hampu.wherehaveibeen.ui.theme.LocalAppColorTokens

@Composable
fun CountryRow(
    country: Country,
    onVisitedToggle: (String) -> Unit,
    onWishlistToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColorTokens.current

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = country.flagEmoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = country.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${country.isoCode} - ${country.capital}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = country.continent,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { onVisitedToggle(country.isoCode) },
                modifier = Modifier.semantics {
                    contentDescription = "Toggle visited for ${country.name}"
                }
            ) {
                Icon(
                    imageVector = if (country.isVisited) {
                        Icons.Filled.CheckCircle
                    } else {
                        Icons.Outlined.CheckCircleOutline
                    },
                    contentDescription = null,
                    tint = if (country.isVisited) appColors.visited else Color.Gray
                )
            }
            IconButton(
                onClick = { onWishlistToggle(country.isoCode) },
                modifier = Modifier.semantics {
                    contentDescription = "Toggle wishlist for ${country.name}"
                }
            ) {
                Icon(
                    imageVector = if (country.isWishlisted) {
                        Icons.Outlined.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = null,
                    tint = if (country.isWishlisted) appColors.wishlist else Color.Gray
                )
            }
        }
    }
}
