package com.hampu.wherehaveibeen.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.hampu.wherehaveibeen.data.settings.AppSettings

data class AppColorTokens(
    val visited: Color,
    val wishlist: Color,
    val mapBase: Color
)

val LocalAppColorTokens = staticCompositionLocalOf {
    AppColorTokens(
        visited = Color.Unspecified,
        wishlist = Color.Unspecified,
        mapBase = Color.Unspecified
    )
}

@Composable
fun WhereHaveIBeenTheme(
    settings: AppSettings,
    content: @Composable () -> Unit
) {
    val colors = if (settings.useDarkTheme) {
        darkColorScheme(
            primary = settings.accentColor,
            onPrimary = Night,
            secondary = Steel,
            tertiary = settings.wishlistColor,
            background = Night,
            onBackground = Cloud,
            surface = DeepSea,
            onSurface = Cloud,
            surfaceVariant = Slate,
            onSurfaceVariant = Steel
        )
    } else {
        lightColorScheme(
            primary = settings.accentColor,
            onPrimary = Cloud,
            secondary = Steel,
            tertiary = settings.wishlistColor,
            background = Cloud,
            onBackground = Night,
            surface = Color(0xFFF8FBFF),
            onSurface = Night,
            surfaceVariant = Color(0xFFDDE7F0),
            onSurfaceVariant = Slate
        )
    }

    CompositionLocalProvider(
        LocalAppColorTokens provides AppColorTokens(
            visited = settings.visitedColor,
            wishlist = settings.wishlistColor,
            mapBase = if (settings.useDarkTheme) {
                lerp(settings.mapBaseColor, Cloud, 0.22f)
            } else {
                settings.mapBaseColor
            }
        )
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = AppTypography,
            content = content
        )
    }
}
