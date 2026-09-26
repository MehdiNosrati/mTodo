package io.mns.base.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.Priority
import io.mns.base.app.data.RepeatInterval
import io.mns.base.app.data.Subtask
import io.mns.base.app.data.TodoItem
import io.mns.base.app.ui.viewmodels.TodoDetailViewModel
import io.mns.base.app.util.DateTimeHelper
import io.mns.base.app.util.generateRandomUuid
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

private val Brand1 = Color(0xFF6366F1)
private val HighPriorityColor = Color(0xFFEF4444)
private val MediumPriorityColor = Color(0xFFF59E0B)
private val LowPriorityColor = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedTodoDetailScreen(
    id: String? = null,
    mode: String = "create",
    draftTitle: String? = null,
    viewModel: TodoDetailViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val todoItem by viewModel.todoItem.collectAsState()
    val doneItem by viewModel.doneItem.collectAsState()

    var title by remember { mutableStateOf(draftTitle ?: "") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.NONE) }
    var category by remember { mutableStateOf("General") }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var repeatInterval by remember { mutableStateOf(RepeatInterval.NONE) }
    var subtasks by remember { mutableStateOf<List<Subtask>>(emptyList()) }
    var newSubtaskText by remember { mutableStateOf("") }
    var isPinned by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(id, mode) {
        if (!id.isNullOrBlank()) {
            if (mode == "readonly") {
                viewModel.loadDone(id)
            } else {
                viewModel.loadTodo(id)
            }
        }
    }

    LaunchedEffect(todoItem) {
        val item = todoItem
        if (item != null && mode != "readonly") {
            title = item.title
            description = item.description
            priority = item.priority
            category = item.category
            dueDate = item.dueDate
            repeatInterval = item.repeatInterval
            subtasks = item.subtasks
            isPinned = item.isPinned
        }
    }

    LaunchedEffect(doneItem) {
        val item = doneItem
        if (item != null && mode == "readonly") {
            title = item.title
            description = item.description
            priority = item.priority
            category = item.category
            dueDate = item.dueDate
            repeatInterval = item.repeatInterval
            subtasks = item.subtasks
            isPinned = item.isPinned
        }
    }

    val isReadOnly = mode == "readonly"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (mode) {
                            "readonly" -> "Task Details"
                            "edit" -> "Edit Task"
                            else -> "New Task"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isReadOnly) {
                        TextButton(
                            onClick = {
                                if (title.isNotBlank()) {
                                    viewModel.saveTodo(
                                        id = id,
                                        title = title,
                                        description = description,
                                        dueDate = dueDate,
                                        priority = priority,
                                        category = category,
                                        subtasks = subtasks,
                                        repeatInterval = repeatInterval,
                                        isPinned = isPinned,
                                        onComplete = onBack
                                    )
                                }
                            }
                        ) {
                            Text("Save", fontWeight = FontWeight.Bold, color = Brand1)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                readOnly = isReadOnly,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                readOnly = isReadOnly,
                minLines = 3,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Priority Selector
            Text(
                "Priority",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.entries.forEach { p ->
                    val selected = priority == p
                    val color = when (p) {
                        Priority.HIGH -> HighPriorityColor
                        Priority.MEDIUM -> MediumPriorityColor
                        Priority.LOW -> LowPriorityColor
                        Priority.NONE -> MaterialTheme.colorScheme.outline
                    }
                    FilterChip(
                        selected = selected,
                        onClick = { if (!isReadOnly) priority = p },
                        label = { Text(p.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Category Selector
            Text(
                "Category",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            val categories = listOf("General", "Work", "Personal", "Shopping", "Health")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = category.equals(cat, ignoreCase = true),
                        onClick = { if (!isReadOnly) category = cat },
                        label = { Text(cat) }
                    )
                }
            }

            // Due Date Section
            Text(
                "Due Date & Time",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentDue = dueDate
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Brand1
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (currentDue != null) formatDetailTimestamp(currentDue) else "No due date set",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (!isReadOnly) {
                        if (currentDue != null) {
                            IconButton(onClick = { dueDate = null }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Clear date")
                            }
                        } else {
                            TextButton(onClick = {
                                // Set to tomorrow 10am by default
                                dueDate = DateTimeHelper.currentTimeMillis() + 86400000L
                            }) {
                                Text("Set Tomorrow")
                            }
                        }
                    }
                }
            }

            // Repeat Interval
            Text(
                "Repeat",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(RepeatInterval.entries) { rep ->
                    FilterChip(
                        selected = repeatInterval == rep,
                        onClick = { if (!isReadOnly) repeatInterval = rep },
                        label = { Text(rep.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            // Subtasks Checklist
            Text(
                "Subtasks (${subtasks.count { it.isDone }}/${subtasks.size})",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                subtasks.forEachIndexed { index, subtask ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = subtask.isDone,
                            onCheckedChange = { checked ->
                                if (!isReadOnly) {
                                    val updated = subtasks.toMutableList()
                                    updated[index] = subtask.copy(isDone = checked)
                                    subtasks = updated
                                }
                            }
                        )
                        Text(
                            text = subtask.title,
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = if (subtask.isDone) TextDecoration.LineThrough else null,
                            color = if (subtask.isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (!isReadOnly) {
                            IconButton(
                                onClick = {
                                    subtasks = subtasks.filterIndexed { i, _ -> i != index }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove subtask", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                if (!isReadOnly) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newSubtaskText,
                            onValueChange = { newSubtaskText = it },
                            placeholder = { Text("Add subtask...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (newSubtaskText.isNotBlank()) {
                                    subtasks = subtasks + Subtask(
                                        id = generateRandomUuid(),
                                        title = newSubtaskText.trim(),
                                        isDone = false
                                    )
                                    newSubtaskText = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add subtask", tint = Brand1)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Delete / Complete Action Buttons for existing tasks
            if (todoItem != null && !isReadOnly) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.deleteTodo(todoItem!!, onComplete = onBack)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete")
                    }

                    Button(
                        onClick = {
                            viewModel.completeTodo(todoItem!!, onComplete = onBack)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LowPriorityColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Complete")
                    }
                }
            }

            if (doneItem != null && isReadOnly) {
                OutlinedButton(
                    onClick = {
                        viewModel.deleteDone(doneItem!!, onComplete = onBack)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete Permanently")
                }
            }
        }
    }
}

private fun formatDetailTimestamp(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val month = local.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    val minute = local.minute.toString().padStart(2, '0')
    val hour = local.hour
    val amPm = if (hour >= 12) "PM" else "AM"
    val displayHour = if (hour % 12 == 0) 12 else hour % 12
    return "$month ${local.dayOfMonth}, ${local.year} · $displayHour:$minute $amPm"
}
