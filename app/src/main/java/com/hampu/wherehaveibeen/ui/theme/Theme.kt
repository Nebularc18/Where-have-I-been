package com.hampu.wherehaveibeen.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = OceanBlue,
    onPrimary = Mist,
    secondary = Sand,
    tertiary = Coral,
    surface = Mist,
    background = Mist
)

@Composable
fun WhereHaveIBeenTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
