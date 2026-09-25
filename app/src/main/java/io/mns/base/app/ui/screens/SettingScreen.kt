package io.mns.base.app.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.SortOrder
import io.mns.base.app.data.TodoItem
import io.mns.base.app.ui.viewmodels.SettingViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private val SettingsBrand1 = Color(0xFF8B5CF6)
private val SettingsBrand2 = Color(0xFFEC4899)
private val SettingsBrush = Brush.linearGradient(
    colors = listOf(SettingsBrand1, SettingsBrand2),
    start = Offset.Zero,
    end = Offset.Infinite
)

private val DataBrush = Brush.linearGradient(
    colors = listOf(Color(0xFF3B82F6), Color(0xFF06B6D4)),
    start = Offset.Zero,
    end = Offset.Infinite
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    isDark: Boolean,
    onBack: () -> Unit,
    onToggleTheme: () -> Unit,
    viewModel: SettingViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val sortOrder by viewModel.sortOrder.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState()
    val trashedTodos by viewModel.trashedTodos.observeAsState(emptyList())
    val trashedDoneItems by viewModel.trashedDoneItems.observeAsState(emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var notificationsEnabled by remember {
        mutableStateOf(viewModel.areNotificationsEnabled())
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notificationsEnabled = viewModel.areNotificationsEnabled()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationsEnabled = isGranted || viewModel.areNotificationsEnabled()
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            viewModel.exportBackup(uri) { success, msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.restoreBackup(uri) { success, msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }
        }
    }

    SettingScreenContent(
        isDark = isDark,
        sortOrder = sortOrder,
        dailyGoal = dailyGoal,
        notificationsEnabled = notificationsEnabled,
        trashedTodos = trashedTodos,
        trashedDoneItems = trashedDoneItems,
        onRequestNotificationPermission = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                try {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_SETTINGS)
                    context.startActivity(intent)
                }
            }
        },
        onOpenNotificationSettings = {
            try {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)
            }
        },
        onDailyGoalChange = { viewModel.setDailyGoal(it) },
        onRestoreTodo = { viewModel.restoreTodoFromTrash(it) },
        onRestoreDone = { viewModel.restoreDoneFromTrash(it) },
        onHardDeleteTodo = { viewModel.hardDeleteTodo(it) },
        onHardDeleteDone = { viewModel.hardDeleteDone(it) },
        onEmptyTrash = { viewModel.emptyTrash() },
        onBack = onBack,
        onToggleTheme = {
            viewModel.toggleTheme()
            onToggleTheme()
        },
        onSortOrderChange = { viewModel.setSortOrder(it) },
        onExportClick = {
            val time = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
            exportLauncher.launch("mtodo_backup_$time.json")
        },
        onRestoreClick = {
            restoreLauncher.launch(arrayOf("application/json", "*/*"))
        },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreenContent(
    isDark: Boolean,
    sortOrder: SortOrder = SortOrder.CREATION_DATE_DESC,
    dailyGoal: Int = 3,
    notificationsEnabled: Boolean = true,
    trashedTodos: List<TodoItem> = emptyList(),
    trashedDoneItems: List<DoneItem> = emptyList(),
    onRequestNotificationPermission: () -> Unit = {},
    onOpenNotificationSettings: () -> Unit = {},
    onDailyGoalChange: (Int) -> Unit = {},
    onRestoreTodo: (TodoItem) -> Unit = {},
    onRestoreDone: (DoneItem) -> Unit = {},
    onHardDeleteTodo: (TodoItem) -> Unit = {},
    onHardDeleteDone: (DoneItem) -> Unit = {},
    onEmptyTrash: () -> Unit = {},
    onBack: () -> Unit = {},
    onToggleTheme: () -> Unit = {},
    onSortOrderChange: (SortOrder) -> Unit = {},
    onExportClick: () -> Unit = {},
    onRestoreClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    animate: Boolean = true
) {
    var showSortDialog by remember { mutableStateOf(false) }
    var showDailyGoalDialog by remember { mutableStateOf(false) }
    var showTrashDialog by remember { mutableStateOf(false) }
    var showConfirmEmptyTrash by remember { mutableStateOf(false) }

    val totalTrashed = trashedTodos.size + trashedDoneItems.size

    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = { Text("Default Sort Order", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SortOrder.entries.forEach { order ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onSortOrderChange(order)
                                    showSortDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (order == sortOrder),
                                onClick = {
                                    onSortOrderChange(order)
                                    showSortDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = order.label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSortDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDailyGoalDialog) {
        AlertDialog(
            onDismissRequest = { showDailyGoalDialog = false },
            title = { Text("Daily Completion Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(1, 2, 3, 5, 8, 10).forEach { target ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onDailyGoalChange(target)
                                    showDailyGoalDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (target == dailyGoal),
                                onClick = {
                                    onDailyGoalChange(target)
                                    showDailyGoalDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$target ${if (target == 1) "task" else "tasks"} per day",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (target == dailyGoal) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDailyGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showConfirmEmptyTrash) {
        AlertDialog(
            onDismissRequest = { showConfirmEmptyTrash = false },
            title = { Text("Empty Trash?", fontWeight = FontWeight.Bold) },
            text = {
                Text("All $totalTrashed items in the trash will be permanently deleted. This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEmptyTrash()
                        showConfirmEmptyTrash = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Empty Trash")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmEmptyTrash = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showTrashDialog) {
        AlertDialog(
            onDismissRequest = { showTrashDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recycle Bin ($totalTrashed)", fontWeight = FontWeight.Bold)
                    if (totalTrashed > 0) {
                        TextButton(
                            onClick = { showConfirmEmptyTrash = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Empty", fontSize = 13.sp)
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
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (totalTrashed == 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Recycle bin is empty",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
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
                                    onRestore = { onRestoreTodo(todo) },
                                    onDeleteForever = { onHardDeleteTodo(todo) }
                                )
                            }
                            items(trashedDoneItems, key = { "done_${it.id}" }) { done ->
                                TrashedItemRow(
                                    title = done.title,
                                    isDone = true,
                                    category = done.category,
                                    onRestore = { onRestoreDone(done) },
                                    onDeleteForever = { onHardDeleteDone(done) }
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = TextStyle(
                            brush = SettingsBrush,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section: Appearance
            AnimatedSettingsSection(delayMs = 60L, animate = animate) {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.Settings,
                        iconBrush = SettingsBrush,
                        title = "Dark Mode",
                        subtitle = "Switch between light and dark theme"
                    ) {
                        Switch(
                            checked = isDark,
                            onCheckedChange = { onToggleTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SettingsBrand1,
                                uncheckedThumbColor = Color.White,
                                uncheckedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
            }

            // Section: Notifications & Reminders
            AnimatedSettingsSection(delayMs = 80L, animate = animate) {
                Text(
                    text = "Notifications & Reminders",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                SettingsCard {
                    if (notificationsEnabled) {
                        SettingsRow(
                            icon = Icons.Default.NotificationsActive,
                            iconBrush = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669))),
                            title = "Task Reminders",
                            subtitle = "Notifications active · Due date alerts enabled"
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(Color(0xFF10B981), CircleShape)
                                    )
                                    Text(
                                        text = "Active",
                                        color = Color(0xFF059669),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        SettingsRow(
                            icon = Icons.Default.NotificationsOff,
                            iconBrush = Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFF59E0B))),
                            title = "Notifications Disabled",
                            subtitle = "Enable notifications so due date alerts can ring"
                        ) {
                            FilledTonalButton(
                                onClick = onRequestNotificationPermission,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Text("Enable", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Section: Tasks & Productivity
            AnimatedSettingsSection(delayMs = 100L, animate = animate) {
                Text(
                    text = "Tasks & Productivity",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.Sort,
                        iconBrush = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF6366F1))),
                        title = "Default Sort Order",
                        subtitle = sortOrder.label
                    ) {
                        IconButton(onClick = { showSortDialog = true }) {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Change sort order")
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    SettingsRow(
                        icon = Icons.Default.Flag,
                        iconBrush = Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))),
                        title = "Daily Completion Goal",
                        subtitle = "$dailyGoal tasks / day"
                    ) {
                        IconButton(onClick = { showDailyGoalDialog = true }) {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Change daily goal")
                        }
                    }
                }
            }

            // Section: Data & Storage
            AnimatedSettingsSection(delayMs = 120L, animate = animate) {
                Text(
                    text = "Data Management",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.DeleteSweep,
                        iconBrush = Brush.linearGradient(listOf(Color(0xFF6B7280), Color(0xFF4B5563))),
                        title = "Recycle Bin",
                        subtitle = if (totalTrashed == 0) "Trash is empty" else "$totalTrashed deleted ${if (totalTrashed == 1) "item" else "items"}"
                    ) {
                        FilledTonalButton(
                            onClick = { showTrashDialog = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("View", fontSize = 12.sp)
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    SettingsRow(
                        icon = Icons.Default.CloudUpload,
                        iconBrush = DataBrush,
                        title = "Export Backup (JSON)",
                        subtitle = "Save all active and completed tasks"
                    ) {
                        FilledTonalButton(
                            onClick = onExportClick,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Export", fontSize = 12.sp)
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    SettingsRow(
                        icon = Icons.Default.CloudDownload,
                        iconBrush = Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFFF43F5E))),
                        title = "Restore from Backup",
                        subtitle = "Import tasks from a JSON backup file"
                    ) {
                        FilledTonalButton(
                            onClick = onRestoreClick,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Restore", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Section: About
            AnimatedSettingsSection(delayMs = 150L, animate = animate) {
                SettingsCard {
                    SettingsRow(
                        icon = Icons.Default.Info,
                        iconBrush = Brush.linearGradient(
                            colors = listOf(Color(0xFF3B82F6), Color(0xFF06B6D4)),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        ),
                        title = "mTodo v2.5.0",
                        subtitle = "Offline-first, private & distraction-free"
                    ) {}
                }
            }
        }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (isDone) "Done" else "Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isDone) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                )
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
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Restore,
                contentDescription = "Restore item",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }

        IconButton(
            onClick = onDeleteForever,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = "Delete forever",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AnimatedSettingsSection(
    delayMs: Long,
    animate: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    var visible by remember { mutableStateOf(!animate) }
    LaunchedEffect(Unit) {
        if (animate) {
            delay(delayMs)
            visible = true
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = SettingsBrand1.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 4.dp),
        content = content
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconBrush: Brush,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = SettingsBrand1.copy(alpha = 0.2f)
                )
                .background(iconBrush, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
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
