package com.hampu.wherehaveibeen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hampu.wherehaveibeen.data.repository.CountryRepository
import com.hampu.wherehaveibeen.data.settings.AppSettingsRepository
import com.hampu.wherehaveibeen.navigation.TopLevelDestination
import com.hampu.wherehaveibeen.ui.list.CountryListScreen
import com.hampu.wherehaveibeen.ui.list.CountryListViewModel
import com.hampu.wherehaveibeen.ui.map.MapScreen
import com.hampu.wherehaveibeen.ui.map.MapViewModel
import com.hampu.wherehaveibeen.ui.settings.SettingsScreen
import com.hampu.wherehaveibeen.ui.settings.SettingsViewModel
import com.hampu.wherehaveibeen.ui.stats.StatsScreen
import com.hampu.wherehaveibeen.ui.stats.StatsViewModel
import com.hampu.wherehaveibeen.ui.wishlist.WishlistScreen
import com.hampu.wherehaveibeen.ui.wishlist.WishlistViewModel

@Composable
fun WhereHaveIBeenApp(
    repository: CountryRepository,
    appSettingsRepository: AppSettingsRepository,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val factory = remember(repository, appSettingsRepository) {
        AppViewModelFactory(repository, appSettingsRepository)
    }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                TopLevelDestination.entries.forEach { destination ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TopLevelDestination.Map.route) {
                val viewModel: MapViewModel = viewModel(factory = factory)
                MapScreen(viewModel = viewModel)
            }
            composable(TopLevelDestination.List.route) {
                val viewModel: CountryListViewModel = viewModel(factory = factory)
                CountryListScreen(viewModel = viewModel)
            }
            composable(TopLevelDestination.Stats.route) {
                val viewModel: StatsViewModel = viewModel(factory = factory)
                StatsScreen(viewModel = viewModel)
            }
            composable(TopLevelDestination.Wishlist.route) {
                val viewModel: WishlistViewModel = viewModel(factory = factory)
                WishlistScreen(viewModel = viewModel)
            }
            composable(TopLevelDestination.Settings.route) {
                val viewModel: SettingsViewModel = viewModel(factory = factory)
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
