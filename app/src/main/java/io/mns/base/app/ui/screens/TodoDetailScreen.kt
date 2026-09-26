package io.mns.base.app.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.Priority
import io.mns.base.app.data.RepeatInterval
import io.mns.base.app.data.Subtask
import io.mns.base.app.data.TodoItem
import io.mns.base.app.ui.viewmodels.TodoDetailViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private val Brand1 = Color(0xFF6366F1)
private val Brand2 = Color(0xFFA78BFA)
private val DoneGreen = Color(0xFF10B981)

private fun brandBrush() = Brush.linearGradient(
    colors = listOf(Brand1, Brand2),
    start = Offset.Zero,
    end = Offset.Infinite
)

private val SUGGESTED_TAGS = listOf("Work", "Personal", "Urgent", "Shopping", "Health", "Study")
private val DEFAULT_CATEGORIES = listOf("General", "Work", "Personal", "Shopping", "Health")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    id: String?,
    mode: String,
    draftTitle: String? = null,
    onBack: () -> Unit,
    viewModel: TodoDetailViewModel = koinViewModel()
) {
    val todoItem by viewModel.todoItem.collectAsState()
    val doneItem by viewModel.doneItem.collectAsState()

    LaunchedEffect(id, mode) {
        if (id != null) {
            if (mode == "readonly") {
                viewModel.loadDone(id)
            } else {
                viewModel.loadTodo(id)
            }
        }
    }

    TodoDetailScreenContent(
        id = id,
        mode = mode,
        draftTitle = draftTitle,
        todoItem = todoItem,
        doneItem = doneItem,
        onBack = onBack,
        onSave = { title, desc, dueDate, priority, tags, subtasks, repeatInterval, isPinned, category ->
            viewModel.saveTodo(
                id = id,
                title = title,
                description = desc,
                dueDate = dueDate,
                priority = priority,
                tags = tags,
                subtasks = subtasks,
                repeatInterval = repeatInterval,
                isPinned = isPinned,
                category = category,
                onComplete = onBack
            )
        },
        onCompleteTodo = { todo ->
            viewModel.completeTodo(todo, onComplete = onBack)
        },
        onDeleteTodo = { todo ->
            viewModel.deleteTodo(todo, onComplete = onBack)
        },
        onDeleteDone = { done ->
            viewModel.deleteDone(done, onComplete = onBack)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TodoDetailScreenContent(
    id: String?,
    mode: String,
    draftTitle: String? = null,
    todoItem: TodoItem? = null,
    doneItem: DoneItem? = null,
    onBack: () -> Unit = {},
    onSave: (
        title: String,
        desc: String,
        dueDate: Long?,
        priority: Priority,
        tags: List<String>,
        subtasks: List<Subtask>,
        repeatInterval: RepeatInterval,
        isPinned: Boolean,
        category: String
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onCompleteTodo: (TodoItem) -> Unit = {},
    onDeleteTodo: (TodoItem) -> Unit = {},
    onDeleteDone: (DoneItem) -> Unit = {}
) {
    val isReadOnly = mode == "readonly"
    val isEdit = mode == "edit"

    var title by remember { mutableStateOf(draftTitle.orEmpty()) }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.NONE) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var newTagInput by remember { mutableStateOf("") }
    var showCustomTagField by remember { mutableStateOf(false) }
    var subtasks by remember { mutableStateOf<List<Subtask>>(emptyList()) }
    var newSubtaskText by remember { mutableStateOf("") }
    var repeatInterval by remember { mutableStateOf(RepeatInterval.NONE) }
    var isPinned by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("General") }

    var showFocusDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotificationPermission = granted
    }

    // Populate state once data is loaded
    LaunchedEffect(todoItem) {
        todoItem?.let {
            title = it.title
            description = it.description
            priority = it.priority
            dueDate = it.dueDate
            tags = it.tags
            subtasks = it.subtasks
            repeatInterval = it.repeatInterval
            isPinned = it.isPinned
            category = it.category
        }
    }

    LaunchedEffect(doneItem) {
        doneItem?.let {
            title = it.title
            description = it.description
            priority = it.priority
            dueDate = it.dueDate
            tags = it.tags
            subtasks = it.subtasks
            repeatInterval = it.repeatInterval
            isPinned = it.isPinned
            category = it.category
        }
    }

    fun showDateTimePicker() {
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val calendar = Calendar.getInstance()
        dueDate?.let { calendar.timeInMillis = it }
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _, year, month, dayOfMonth ->
            val timeCal = Calendar.getInstance()
            TimePickerDialog(context, { _, hourOfDay, minute ->
                val resultCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                dueDate = resultCal.timeInMillis
            }, timeCal.get(Calendar.HOUR_OF_DAY), timeCal.get(Calendar.MINUTE), false).show()
        }, currentYear, currentMonth, currentDay).show()
    }

    if (showFocusDialog) {
        PomodoroTimerDialog(
            taskTitle = title.ifBlank { "Focus Session" },
            onDismiss = { showFocusDialog = false },
            onCompleteTask = {
                showFocusDialog = false
                if (todoItem != null) {
                    onCompleteTodo(todoItem)
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (mode) {
                            "readonly" -> "Task Details"
                            "edit" -> "Edit Task"
                            else -> "New Task"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    if (isEdit && todoItem != null) {
                        IconButton(onClick = { showFocusDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Focus Timer",
                                tint = Brand1
                            )
                        }
                        IconButton(onClick = { onDeleteTodo(todoItem) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        IconButton(
                            onClick = {
                                if (title.isNotBlank()) {
                                    onSave(title, description, dueDate, priority, tags, subtasks, repeatInterval, isPinned, category)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else if (isReadOnly && doneItem != null) {
                        IconButton(onClick = { onDeleteDone(doneItem) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    } else if (!isReadOnly) {
                        IconButton(
                            enabled = title.isNotBlank(),
                            onClick = {
                                if (title.isNotBlank()) {
                                    onSave(title, description, dueDate, priority, tags, subtasks, repeatInterval, isPinned, category)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save",
                                tint = if (title.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task Title Input / Display
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (isReadOnly) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isPinned) {
                                    Text("📌", fontSize = 14.sp)
                                }
                                Text(
                                    text = title.ifBlank { "Untitled Task" },
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = { Text("What needs to be done?") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Brand1,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )

                            // Pin Button
                            IconButton(
                                onClick = { isPinned = !isPinned },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "Pin Task",
                                    tint = if (isPinned) Brand1 else MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    // Description / Notes
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notes,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp)
                        )
                        if (isReadOnly) {
                            Text(
                                text = description.ifBlank { "No additional notes" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (description.isNotBlank()) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                placeholder = { Text("Add details, notes, or context...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                maxLines = 6,
                                textStyle = MaterialTheme.typography.bodyMedium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }

            // Category / Project Space Section
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = Brand1,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (isReadOnly) {
                        Text(
                            text = "📁 $category",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = Brand1
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DEFAULT_CATEGORIES.forEach { cat ->
                                val selected = category.equals(cat, ignoreCase = true)
                                FilterChip(
                                    selected = selected,
                                    onClick = { category = cat },
                                    label = { Text("📁 $cat") },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Priority Section
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = priority.color,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Priority",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (isReadOnly) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = priority.color.copy(alpha = 0.15f),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(priority.color, CircleShape)
                                )
                                Text(
                                    text = priority.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = priority.color
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Priority.entries.forEach { p ->
                                val selected = priority == p
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { priority = p },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selected) p.color.copy(alpha = 0.20f) else MaterialTheme.colorScheme.surface,
                                    border = if (selected) {
                                        androidx.compose.foundation.BorderStroke(1.5.dp, p.color)
                                    } else {
                                        androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(p.color, CircleShape)
                                        )
                                        Text(
                                            text = p.label,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = if (selected) p.color else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Due Date & Time Section with Notification Permission Warning
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Brand1,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Due Date & Time",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (dueDate != null) {
                        val formatted = SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(dueDate!!))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Brand1.copy(alpha = 0.10f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Brand1.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = formatted,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (!isReadOnly) {
                                    IconButton(
                                        onClick = { dueDate = null },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove due date",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Notification Permission Prompt if needed
                        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Enable notifications to receive due date alarms",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Enable", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No due date set",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!isReadOnly) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SuggestionChip(
                                onClick = {
                                    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    val cal = Calendar.getInstance().apply {
                                        set(Calendar.HOUR_OF_DAY, 18)
                                        set(Calendar.MINUTE, 0)
                                        set(Calendar.SECOND, 0)
                                    }
                                    dueDate = cal.timeInMillis
                                },
                                label = { Text("Today 6 PM") }
                            )
                            SuggestionChip(
                                onClick = {
                                    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    val cal = Calendar.getInstance().apply {
                                        add(Calendar.DAY_OF_YEAR, 1)
                                        set(Calendar.HOUR_OF_DAY, 18)
                                        set(Calendar.MINUTE, 0)
                                        set(Calendar.SECOND, 0)
                                    }
                                    dueDate = cal.timeInMillis
                                },
                                label = { Text("Tomorrow") }
                            )
                            SuggestionChip(
                                onClick = { showDateTimePicker() },
                                label = { Text("Pick...") }
                            )
                        }
                    }
                }
            }

            // Recurring / Repeat Schedule Section
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = null,
                            tint = Brand1,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Repeat Task",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (isReadOnly) {
                        Text(
                            text = repeatInterval.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RepeatInterval.entries.forEach { interval ->
                                val selected = repeatInterval == interval
                                FilterChip(
                                    selected = selected,
                                    onClick = { repeatInterval = interval },
                                    label = { Text(interval.label) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Tags Section
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = Brand2,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Tags",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Active tags
                    if (tags.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tags.forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Brand1.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Brand1.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "#$tag",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = Brand1
                                        )
                                        if (!isReadOnly) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove $tag",
                                                tint = Brand1,
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable { tags = tags.filter { it != tag } }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else if (isReadOnly) {
                        Text(
                            text = "No tags",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!isReadOnly) {
                        val availableSuggestions = SUGGESTED_TAGS.filter { !tags.contains(it) }
                        if (availableSuggestions.isNotEmpty()) {
                            Text(
                                text = "Suggestions",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                availableSuggestions.forEach { suggestion ->
                                    SuggestionChip(
                                        onClick = { tags = tags + suggestion },
                                        label = { Text("+$suggestion") },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }

                        if (showCustomTagField) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = newTagInput,
                                    onValueChange = { newTagInput = it },
                                    placeholder = { Text("Custom tag name") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        val clean = newTagInput.trim().replace("#", "")
                                        if (clean.isNotBlank() && !tags.contains(clean)) {
                                            tags = tags + clean
                                            newTagInput = ""
                                            showCustomTagField = false
                                        }
                                        focusManager.clearFocus()
                                    })
                                )
                                Button(
                                    onClick = {
                                        val clean = newTagInput.trim().replace("#", "")
                                        if (clean.isNotBlank() && !tags.contains(clean)) {
                                            tags = tags + clean
                                            newTagInput = ""
                                            showCustomTagField = false
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Add")
                                }
                            }
                        } else {
                            TextButton(
                                onClick = { showCustomTagField = true },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("+ Add custom tag", color = Brand1)
                            }
                        }
                    }
                }
            }

            // Subtasks Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val completedCount = subtasks.count { it.isDone }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatListBulleted,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Subtasks",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (subtasks.isNotEmpty()) {
                            Text(
                                text = "$completedCount of ${subtasks.size}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (subtasks.isNotEmpty()) {
                        LinearProgressIndicator(
                            progress = { if (subtasks.isEmpty()) 0f else completedCount.toFloat() / subtasks.size },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = DoneGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    // Subtask items
                    subtasks.forEachIndexed { index, subtask ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = subtask.isDone,
                                onCheckedChange = if (isReadOnly) null else { isChecked ->
                                    subtasks = subtasks.toMutableList().also { list ->
                                        list[index] = subtask.copy(isDone = isChecked)
                                    }
                                },
                                enabled = !isReadOnly,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = DoneGreen,
                                    checkmarkColor = Color.White
                                )
                            )
                            Text(
                                text = subtask.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textDecoration = if (subtask.isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                ),
                                color = if (subtask.isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 4.dp)
                            )
                            if (!isReadOnly) {
                                IconButton(
                                    onClick = {
                                        subtasks = subtasks.toMutableList().also { it.removeAt(index) }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete subtask",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Add subtask inline input
                    if (!isReadOnly) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newSubtaskText,
                                onValueChange = { newSubtaskText = it },
                                placeholder = { Text("Add a subtask...", style = MaterialTheme.typography.bodySmall) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        val trimmed = newSubtaskText.trim()
                                        if (trimmed.isNotBlank()) {
                                            subtasks = subtasks + Subtask(title = trimmed)
                                            newSubtaskText = ""
                                        }
                                    }
                                )
                            )
                            IconButton(
                                onClick = {
                                    val trimmed = newSubtaskText.trim()
                                    if (trimmed.isNotBlank()) {
                                        subtasks = subtasks + Subtask(title = trimmed)
                                        newSubtaskText = ""
                                    }
                                },
                                enabled = newSubtaskText.isNotBlank(),
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        if (newSubtaskText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add subtask",
                                    tint = if (newSubtaskText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Timestamps info for Read-Only / Completed task
            if (isReadOnly && doneItem != null) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val doneTimeFormatted = SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(doneItem.doneAt))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                tint = DoneGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Completed on $doneTimeFormatted",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = DoneGreen
                            )
                        }

                        if (doneItem.createdAt > 0L) {
                            val createdFormatted = SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(doneItem.createdAt))
                            Text(
                                text = "Created on $createdFormatted",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Bottom Actions
            Spacer(modifier = Modifier.height(10.dp))
            if (isReadOnly && doneItem != null) {
                OutlinedButton(
                    onClick = { onDeleteDone(doneItem) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Completed Task", fontWeight = FontWeight.Bold)
                }
            } else if (isEdit && todoItem != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onCompleteTodo(todoItem) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DoneGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mark Done", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .height(52.dp)
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), spotColor = Brand1.copy(alpha = 0.35f))
                            .clip(RoundedCornerShape(16.dp))
                            .background(brandBrush())
                            .clickable(
                                enabled = title.isNotBlank(),
                                onClick = { onSave(title, description, dueDate, priority, tags, subtasks, repeatInterval, isPinned, category) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Save Changes",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = Brand1.copy(alpha = 0.4f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (title.isNotBlank()) brandBrush()
                            else Brush.linearGradient(listOf(MaterialTheme.colorScheme.outlineVariant, MaterialTheme.colorScheme.outlineVariant))
                        )
                        .clickable(
                            enabled = title.isNotBlank(),
                            onClick = { onSave(title, description, dueDate, priority, tags, subtasks, repeatInterval, isPinned, category) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Create Task",
                        fontWeight = FontWeight.Bold,
                        color = if (title.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PomodoroTimerDialog(
    taskTitle: String,
    onDismiss: () -> Unit,
    onCompleteTask: () -> Unit
) {
    var totalSeconds by remember { mutableStateOf(25 * 60) }
    var secondsLeft by remember { mutableStateOf(totalSeconds) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, secondsLeft) {
        if (isRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        } else if (secondsLeft == 0) {
            isRunning = false
        }
    }

    val fraction = if (totalSeconds > 0) (secondsLeft.toFloat() / totalSeconds).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(targetValue = fraction, label = "timerProgress")

    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Focus Session",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = taskTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brand1,
                    maxLines = 1
                )

                // Timer Circular Progress
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 8.dp,
                        color = Brand1,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 32.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isRunning) "Focusing..." else if (secondsLeft == 0) "Time is up! 🎉" else "Paused",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Preset length chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(15, 25, 45).forEach { mins ->
                        FilterChip(
                            selected = (totalSeconds == mins * 60),
                            onClick = {
                                totalSeconds = mins * 60
                                secondsLeft = totalSeconds
                                isRunning = false
                            },
                            label = { Text("${mins}m") },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            secondsLeft = totalSeconds
                            isRunning = false
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset timer")
                    }

                    FilledIconButton(
                        onClick = { isRunning = !isRunning },
                        modifier = Modifier.size(54.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Brand1)
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isRunning) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Button(
                        onClick = onCompleteTask,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DoneGreen)
                    ) {
                        Text("Finish", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
