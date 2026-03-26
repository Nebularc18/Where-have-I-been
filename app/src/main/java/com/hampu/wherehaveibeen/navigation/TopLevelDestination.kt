package com.hampu.wherehaveibeen.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    Map("map", "Map", Icons.Outlined.Public),
    List("list", "List", Icons.Outlined.List),
    Stats("stats", "Stats", Icons.Outlined.BarChart),
    Wishlist("wishlist", "Wishlist", Icons.Outlined.FavoriteBorder),
    Settings("settings", "Settings", Icons.Outlined.Settings)
}
