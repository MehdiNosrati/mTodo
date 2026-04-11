package io.mns.base.app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Todo", Icons.Default.Home)
    object Done : Screen("done", "Done", Icons.Default.Done)
    object Settings : Screen("settings", "Settings", Icons.Default.Home) // Icon not used for settings in bottom bar
}

@Composable
fun MainScreen(onToggleTheme: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        Screen.Home,
        Screen.Done
    )

    Scaffold(
        bottomBar = {
            if (currentDestination?.route != Screen.Settings.route) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.route == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onSettingsClick = { navController.navigate(Screen.Settings.route) })
            }
            composable(Screen.Done.route) {
                DoneScreen(onSettingsClick = { navController.navigate(Screen.Settings.route) })
            }
            composable(Screen.Settings.route) {
                SettingScreen(
                    onBack = { navController.popBackStack() },
                    onToggleTheme = onToggleTheme
                )
            }
        }
    }
}
