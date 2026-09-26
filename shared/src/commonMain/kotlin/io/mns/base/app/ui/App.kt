package io.mns.base.app.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import io.mns.base.app.ui.screens.SharedDoneScreen
import io.mns.base.app.ui.screens.SharedHomeScreen
import io.mns.base.app.ui.screens.SharedInsightsScreen
import io.mns.base.app.ui.screens.SharedSettingScreen
import io.mns.base.app.ui.screens.SharedTodoDetailScreen
import io.mns.base.app.ui.theme.MTodoTheme

private val NavBrand1 = Color(0xFF6366F1)
private val NavBrand2 = Color(0xFFA78BFA)

enum class TabItem(val title: String, val icon: ImageVector) {
    HOME("Todo", Icons.AutoMirrored.Filled.List),
    DONE("Done", Icons.Default.Done),
    INSIGHTS("Insights", Icons.AutoMirrored.Filled.TrendingUp),
    SETTINGS("Settings", Icons.Default.Settings)
}

data class DetailArgs(
    val id: String? = null,
    val mode: String = "create",
    val draftTitle: String? = null
)

@Composable
fun App(
    darkThemeDefault: Boolean = false
) {
    var isDark by remember { mutableStateOf(darkThemeDefault) }
    var currentTab by remember { mutableStateOf(TabItem.HOME) }
    var detailArgs by remember { mutableStateOf<DetailArgs?>(null) }

    MTodoTheme(darkTheme = isDark) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (detailArgs == null) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    ) {
                        TabItem.entries.forEach { tab ->
                            val selected = currentTab == tab
                            NavigationBarItem(
                                icon = {
                                    if (selected) {
                                        Icon(
                                            imageVector = tab.icon,
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
                                            imageVector = tab.icon,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        color = if (selected) NavBrand1 else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                selected = selected,
                                onClick = { currentTab = tab },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                val detail = detailArgs
                if (detail != null) {
                    SharedTodoDetailScreen(
                        id = detail.id,
                        mode = detail.mode,
                        draftTitle = detail.draftTitle,
                        onBack = { detailArgs = null }
                    )
                } else {
                    when (currentTab) {
                        TabItem.HOME -> {
                            SharedHomeScreen(
                                onItemClick = { todo ->
                                    detailArgs = DetailArgs(id = todo.id, mode = "edit")
                                },
                                onExpandAdd = { draft ->
                                    detailArgs = DetailArgs(mode = "create", draftTitle = draft)
                                }
                            )
                        }
                        TabItem.DONE -> {
                            SharedDoneScreen(
                                onItemClick = { done ->
                                    detailArgs = DetailArgs(id = done.id, mode = "readonly")
                                }
                            )
                        }
                        TabItem.INSIGHTS -> {
                            SharedInsightsScreen()
                        }
                        TabItem.SETTINGS -> {
                            SharedSettingScreen(
                                isDark = isDark,
                                onToggleTheme = { isDark = !isDark }
                            )
                        }
                    }
                }
            }
        }
    }
}
