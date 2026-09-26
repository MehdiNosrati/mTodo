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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mns.base.app.data.SortOrder
import io.mns.base.app.ui.viewmodels.SettingViewModel
import kotlinx.coroutines.launch
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
    val totalTrash = trashedTodos.size + trashedDone.size

    var showTrashDialog by remember { mutableStateOf(false) }
    var showConfirmEmptyTrash by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
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

            // Recycle Bin Section
            item {
                SettingsSection(title = "Data Management") {
                    SettingsRow(
                        icon = Icons.Default.DeleteSweep,
                        title = "Recycle Bin",
                        subtitle = if (totalTrash == 0) {
                            "Recycle bin is empty"
                        } else {
                            "$totalTrash deleted ${if (totalTrash == 1) "item" else "items"} · Tap to view or restore"
                        },
                        onClick = { showTrashDialog = true },
                        trailing = {
                            FilledTonalButton(
                                onClick = { showTrashDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("View", style = MaterialTheme.typography.labelMedium)
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

    // Recycle Bin Dialog
    if (showTrashDialog) {
        AlertDialog(
            onDismissRequest = { showTrashDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recycle Bin ($totalTrash)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (totalTrash > 0) {
                        TextButton(
                            onClick = { showConfirmEmptyTrash = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Empty All", fontSize = 13.sp)
                        }
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                ) {
                    Text(
                        text = "Deleted items are kept for 30 days before being automatically purged.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (totalTrash == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(52.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Recycle bin is empty",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Deleted tasks will appear here",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(trashedTodos, key = { "todo_${it.id}" }) { todo ->
                                TrashedItemRow(
                                    title = todo.title,
                                    isDone = false,
                                    category = todo.category,
                                    onRestore = {
                                        viewModel.restoreTodoFromTrash(todo)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Restored \"${todo.title}\"")
                                        }
                                    },
                                    onDeleteForever = {
                                        viewModel.hardDeleteTodo(todo)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Permanently deleted \"${todo.title}\"")
                                        }
                                    }
                                )
                            }
                            items(trashedDone, key = { "done_${it.id}" }) { done ->
                                TrashedItemRow(
                                    title = done.title,
                                    isDone = true,
                                    category = done.category,
                                    onRestore = {
                                        viewModel.restoreDoneFromTrash(done)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Restored \"${done.title}\"")
                                        }
                                    },
                                    onDeleteForever = {
                                        viewModel.hardDeleteDone(done)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Permanently deleted \"${done.title}\"")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTrashDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Confirmation Alert to Empty Trash
    if (showConfirmEmptyTrash) {
        AlertDialog(
            onDismissRequest = { showConfirmEmptyTrash = false },
            title = {
                Text("Empty Recycle Bin?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("All $totalTrash items in the Recycle Bin will be permanently deleted. This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.emptyTrash()
                        showConfirmEmptyTrash = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Recycle bin emptied")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Empty All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmEmptyTrash = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TrashedItemRow(
    title: String,
    isDone: Boolean,
    category: String,
    onRestore: () -> Unit,
    onDeleteForever: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isDone) {
                            Color(0xFF10B981).copy(alpha = 0.15f)
                        } else {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        }
                    ) {
                        Text(
                            text = if (isDone) "Done" else "Active",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDone) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                    if (category.isNotBlank() && category != "General") {
                        Text(
                            text = "• $category",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            IconButton(
                onClick = onRestore,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = "Restore item",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onDeleteForever,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Delete forever",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
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
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
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
