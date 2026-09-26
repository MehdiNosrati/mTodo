package io.mns.base.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.mns.base.app.data.SortOrder
import io.mns.base.app.ui.viewmodels.SettingViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedSettingScreen(
    viewModel: SettingViewModel = koinViewModel(),
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    val dailyGoal by viewModel.dailyGoal.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val trashedTodos by viewModel.trashedTodos.collectAsState()
    val trashedDone by viewModel.trashedDoneItems.collectAsState()

    var showTrashDialog by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Preferences, habits & data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Appearance Section
            item {
                SettingsSection(title = "Appearance") {
                    SettingsRow(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Theme",
                        subtitle = if (isDark) "Enabled" else "Disabled",
                        trailing = {
                            Switch(
                                checked = isDark,
                                onCheckedChange = { onToggleTheme() }
                            )
                        }
                    )
                }
            }

            // Habits Section
            item {
                SettingsSection(title = "Productivity Goals") {
                    SettingsRow(
                        icon = Icons.Default.Flag,
                        title = "Daily Completion Goal",
                        subtitle = "$dailyGoal tasks per day",
                        trailing = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (dailyGoal > 1) viewModel.setDailyGoal(dailyGoal - 1) },
                                    enabled = dailyGoal > 1
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }
                                Text(
                                    text = dailyGoal.toString(),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                IconButton(
                                    onClick = { if (dailyGoal < 20) viewModel.setDailyGoal(dailyGoal + 1) },
                                    enabled = dailyGoal < 20
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }
                    )
                }
            }

            // Trash Section
            val totalTrash = trashedTodos.size + trashedDone.size
            item {
                SettingsSection(title = "Data Management") {
                    SettingsRow(
                        icon = Icons.Default.DeleteSweep,
                        title = "Trash",
                        subtitle = if (totalTrash == 0) "Trash is empty" else "$totalTrash deleted items (auto-purged after 30 days)",
                        trailing = {
                            if (totalTrash > 0) {
                                TextButton(onClick = { viewModel.emptyTrash() }) {
                                    Text("Empty", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    )
                }
            }

            // App Info Section
            item {
                SettingsSection(title = "About") {
                    SettingsRow(
                        icon = Icons.Default.Info,
                        title = "mTodo",
                        subtitle = "Version 2.5.0 · Kotlin Multiplatform",
                        trailing = {}
                    )
                    SettingsRow(
                        icon = Icons.Default.Security,
                        title = "Privacy",
                        subtitle = "100% offline, zero cloud telemetry or tracking.",
                        trailing = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                content = content
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.size(38.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        trailing()
    }
}
