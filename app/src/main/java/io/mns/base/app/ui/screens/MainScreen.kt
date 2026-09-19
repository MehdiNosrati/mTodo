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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Done
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
    object Home : Screen("home", "Todo", Icons.AutoMirrored.Filled.List)
    object Done : Screen("done", "Done", Icons.Default.Done)
    object Insights : Screen("insights", "Insights", Icons.AutoMirrored.Filled.TrendingUp)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

private val NavBrand1 = Color(0xFF6366F1)
private val NavBrand2 = Color(0xFFA78BFA)

private fun tabIndex(route: String?): Int = when (route) {
    Screen.Home.route -> 0
    Screen.Done.route -> 1
    Screen.Insights.route -> 2
    else -> -1
}

@Composable
fun MainScreen(isDark: Boolean, onToggleTheme: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val tabs = listOf(Screen.Home, Screen.Done, Screen.Insights)
    val showBottomBar = currentRoute in listOf(Screen.Home.route, Screen.Done.route, Screen.Insights.route)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
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
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            enterTransition = {
                val from = tabIndex(initialState.destination.route)
                val to = tabIndex(targetState.destination.route)
                val dir = if (to >= from) 1 else -1
                fadeIn(tween(220)) + slideInHorizontally(
                    initialOffsetX = { dir * it / 8 },
                    animationSpec = tween(220)
                )
            },
            exitTransition = {
                val from = tabIndex(initialState.destination.route)
                val to = tabIndex(targetState.destination.route)
                val dir = if (to >= from) 1 else -1
                fadeOut(tween(180)) + slideOutHorizontally(
                    targetOffsetX = { -dir * it / 8 },
                    animationSpec = tween(180)
                )
            },
            popEnterTransition = {
                val from = tabIndex(initialState.destination.route)
                val to = tabIndex(targetState.destination.route)
                val dir = if (to >= from) 1 else -1
                fadeIn(tween(220)) + slideInHorizontally(
                    initialOffsetX = { dir * it / 8 },
                    animationSpec = tween(220)
                )
            },
            popExitTransition = {
                val from = tabIndex(initialState.destination.route)
                val to = tabIndex(targetState.destination.route)
                val dir = if (to >= from) 1 else -1
                fadeOut(tween(180)) + slideOutHorizontally(
                    targetOffsetX = { -dir * it / 8 },
                    animationSpec = tween(180)
                )
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onItemClick = { todo -> navController.navigate("todo_detail?id=${todo.id}&mode=edit") },
                    onExpandAdd = { draft ->
                        val encoded = android.net.Uri.encode(draft)
                        navController.navigate("todo_detail?mode=create&draftTitle=$encoded")
                    }
                )
            }
            composable(Screen.Done.route) {
                DoneScreen(
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onItemClick = { done -> navController.navigate("todo_detail?id=${done.id}&mode=readonly") }
                )
            }
            composable(Screen.Insights.route) {
                InsightsScreen(onSettingsClick = { navController.navigate(Screen.Settings.route) })
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
            composable(
                route = "todo_detail?id={id}&mode={mode}&draftTitle={draftTitle}",
                arguments = listOf(
                    androidx.navigation.navArgument("id") {
                        type = androidx.navigation.NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    androidx.navigation.navArgument("mode") {
                        type = androidx.navigation.NavType.StringType
                        defaultValue = "create"
                    },
                    androidx.navigation.navArgument("draftTitle") {
                        type = androidx.navigation.NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                ),
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
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                val mode = backStackEntry.arguments?.getString("mode") ?: "create"
                val draftTitle = backStackEntry.arguments?.getString("draftTitle")
                TodoDetailScreen(
                    id = id,
                    mode = mode,
                    draftTitle = draftTitle,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
