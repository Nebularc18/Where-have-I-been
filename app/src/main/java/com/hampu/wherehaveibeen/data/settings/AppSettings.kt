package com.hampu.wherehaveibeen.data.settings

import androidx.compose.ui.graphics.Color

data class AppSettings(
    val useDarkTheme: Boolean = true,
    val accentColor: Color = DefaultAccentColor,
    val visitedColor: Color = DefaultVisitedColor,
    val wishlistColor: Color = DefaultWishlistColor,
    val mapBaseColor: Color = DefaultMapBaseColor
)

val DefaultAccentColor = Color(0xFF63D2FF)
val DefaultVisitedColor = Color(0xFF4DD4AC)
val DefaultWishlistColor = Color(0xFFFFB347)
val DefaultMapBaseColor = Color(0xFF243447)
