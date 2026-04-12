package io.mns.base.app.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Todo", Icons.Default.List)
    object Done : Screen("done", "Done", Icons.Default.Done)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

private val NavBrand1 = Color(0xFF6366F1)
private val NavBrand2 = Color(0xFFA78BFA)

@Composable
fun MainScreen(isDark: Boolean, onToggleTheme: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val tabs = listOf(Screen.Home, Screen.Done)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentRoute != Screen.Settings.route) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    tabs.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                if (selected) {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(NavBrand1, NavBrand2),
                                                    start = Offset.Zero,
                                                    end = Offset.Infinite
                                                ),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .padding(6.dp),
                                        tint = Color.White
                                    )
                                } else {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    color = if (selected) NavBrand1 else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(tween(220)) + slideInHorizontally(
                    initialOffsetX = { it / 10 },
                    animationSpec = tween(220)
                )
            },
            exitTransition = {
                fadeOut(tween(180)) + slideOutHorizontally(
                    targetOffsetX = { -it / 10 },
                    animationSpec = tween(180)
                )
            },
            popEnterTransition = {
                fadeIn(tween(220)) + slideInHorizontally(
                    initialOffsetX = { -it / 10 },
                    animationSpec = tween(220)
                )
            },
            popExitTransition = {
                fadeOut(tween(180)) + slideOutHorizontally(
                    targetOffsetX = { it / 10 },
                    animationSpec = tween(180)
                )
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onSettingsClick = { navController.navigate(Screen.Settings.route) })
            }
            composable(Screen.Done.route) {
                DoneScreen(onSettingsClick = { navController.navigate(Screen.Settings.route) })
            }
            composable(
                route = Screen.Settings.route,
                enterTransition = {
                    fadeIn(tween(250)) + slideInVertically(
                        initialOffsetY = { it / 8 },
                        animationSpec = tween(250)
                    )
                },
                exitTransition = {
                    fadeOut(tween(200)) + slideOutVertically(
                        targetOffsetY = { it / 8 },
                        animationSpec = tween(200)
                    )
                },
                popEnterTransition = {
                    fadeIn(tween(250)) + slideInVertically(
                        initialOffsetY = { it / 8 },
                        animationSpec = tween(250)
                    )
                },
                popExitTransition = {
                    fadeOut(tween(200)) + slideOutVertically(
                        targetOffsetY = { it / 8 },
                        animationSpec = tween(200)
                    )
                }
            ) {
                SettingScreen(
                    isDark = isDark,
                    onBack = { navController.popBackStack() },
                    onToggleTheme = onToggleTheme
                )
            }
        }
    }
}
