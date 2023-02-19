package dev.mahdins.mtodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.mahdins.mtodo.ui.Screen
import dev.mahdins.mtodo.ui.theme.MTodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MTodoTheme {
                Home()
            }
        }
    }
}

@Composable
fun Home() {
    val navController = rememberNavController()
    val bottomNavItems = listOf(Screen.TodoPage, Screen.DonePage)
    Scaffold(bottomBar = {
        BottomNavigation {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            bottomNavItems.forEach { screen ->
                BottomNavigationItem(
                    icon = { Icon(screen.icon, contentDescription = null) },
                    label = { Text(text = stringResource(id = screen.resourceId)) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
            }
        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.TodoPage.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.TodoPage.route) { Text("Todo") }
            composable(Screen.DonePage.route) { Text("Done") }
            composable(Screen.SettingsPage.route) { Text("Settings") }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MTodoTheme {
        Greeting("Android")
    }
}