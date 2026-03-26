package com.hampu.wherehaveibeen.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hampu.wherehaveibeen.ui.components.ScreenHeader

private val AccentPalette = listOf(
    Color(0xFF63D2FF),
    Color(0xFF80ED99),
    Color(0xFFFF8FA3),
    Color(0xFFFFC857),
    Color(0xFFA78BFA)
)

private val VisitedPalette = listOf(
    Color(0xFF4DD4AC),
    Color(0xFF80ED99),
    Color(0xFF4CC9F0),
    Color(0xFFF4A261),
    Color(0xFFE9C46A)
)

private val WishlistPalette = listOf(
    Color(0xFFFFB347),
    Color(0xFFFF8FA3),
    Color(0xFFFFD166),
    Color(0xFFA78BFA),
    Color(0xFFF28482)
)

private val MapBasePalette = listOf(
    Color(0xFF243447),
    Color(0xFF2D4059),
    Color(0xFF324A5F),
    Color(0xFF3A506B),
    Color(0xFF23395B)
)

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onDarkThemeChange = viewModel::setUseDarkTheme,
        onAccentColorChange = viewModel::setAccentColor,
        onVisitedColorChange = viewModel::setVisitedColor,
        onWishlistColorChange = viewModel::setWishlistColor,
        onMapBaseColorChange = viewModel::setMapBaseColor,
        onResetColors = viewModel::resetColors,
        modifier = modifier
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onDarkThemeChange: (Boolean) -> Unit,
    onAccentColorChange: (Color) -> Unit,
    onVisitedColorChange: (Color) -> Unit,
    onWishlistColorChange: (Color) -> Unit,
    onMapBaseColorChange: (Color) -> Unit,
    onResetColors: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenHeader(
            title = "Settings",
            subtitle = "Tune the map colors and overall theme."
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Dark mode", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Keeps the interface easier on the eyes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = uiState.settings.useDarkTheme,
                    onCheckedChange = onDarkThemeChange
                )
            }
        }

        ColorSection(
            title = "Accent color",
            selectedColor = uiState.settings.accentColor,
            palette = AccentPalette,
            onColorSelected = onAccentColorChange
        )
        ColorSection(
            title = "Visited countries",
            selectedColor = uiState.settings.visitedColor,
            palette = VisitedPalette,
            onColorSelected = onVisitedColorChange
        )
        ColorSection(
            title = "Wishlist countries",
            selectedColor = uiState.settings.wishlistColor,
            palette = WishlistPalette,
            onColorSelected = onWishlistColorChange
        )
        ColorSection(
            title = "Map base color",
            selectedColor = uiState.settings.mapBaseColor,
            palette = MapBasePalette,
            onColorSelected = onMapBaseColorChange
        )

        Button(
            onClick = onResetColors,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset to defaults")
        }
    }
}

@Composable
private fun ColorSection(
    title: String,
    selectedColor: Color,
    palette: List<Color>,
    onColorSelected: (Color) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                palette.forEach { color ->
                    ColorSwatch(
                        color = color,
                        selected = color == selectedColor,
                        onClick = { onColorSelected(color) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.material3.Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(999.dp)
                )
                .padding(4.dp)
                .background(color = color, shape = CircleShape)
        )
    }
}
