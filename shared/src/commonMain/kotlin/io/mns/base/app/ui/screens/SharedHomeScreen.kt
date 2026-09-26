package io.mns.base.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mns.base.app.data.Priority
import io.mns.base.app.data.SortOrder
import io.mns.base.app.data.TaskFilter
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoListSection
import io.mns.base.app.ui.viewmodels.HomeViewModel
import io.mns.base.app.util.DateTimeHelper
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

private val Brand1 = Color(0xFF6366F1)
private val Brand2 = Color(0xFFA78BFA)
private val BrandBrush = Brush.linearGradient(
    colors = listOf(Brand1, Brand2),
    start = Offset.Zero,
    end = Offset.Infinite
)
private val HighPriorityColor = Color(0xFFEF4444)
private val MediumPriorityColor = Color(0xFFF59E0B)
private val LowPriorityColor = Color(0xFF10B981)

@Composable
fun SharedHomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onItemClick: (TodoItem) -> Unit,
    onExpandAdd: (String) -> Unit
) {
    val sections by viewModel.sections.collectAsState()
    val availableCategories by viewModel.availableCategories.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedTodoIds by viewModel.selectedTodoIds.collectAsState()

    var isAdding by remember { mutableStateOf(false) }
    var draftText by remember { mutableStateOf("") }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (!isAdding && selectedTodoIds.isEmpty()) {
                FloatingActionButton(
                    onClick = { isAdding = true },
                    containerColor = Brand1,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.shadow(8.dp, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header / App Bar
            if (selectedTodoIds.isNotEmpty()) {
                // Batch Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.clearSelection() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancel")
                    }
                    Text(
                        text = "${selectedTodoIds.size} selected",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.selectAll() }) {
                        Icon(Icons.Default.SelectAll, contentDescription = "Select All")
                    }
                    IconButton(onClick = { viewModel.batchTogglePin() }) {
                        Icon(Icons.Default.PushPin, contentDescription = "Pin")
                    }
                    IconButton(onClick = { viewModel.batchDone() }) {
                        Icon(Icons.Default.Done, contentDescription = "Done")
                    }
                    IconButton(onClick = { viewModel.batchDelete() }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete")
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "mTodo",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Get things done, simply.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Date (Newest first)") },
                                onClick = {
                                    viewModel.setSortOrder(SortOrder.CREATION_DATE_DESC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Due Date") },
                                onClick = {
                                    viewModel.setSortOrder(SortOrder.DUE_DATE_ASC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Priority") },
                                onClick = {
                                    viewModel.setSortOrder(SortOrder.PRIORITY_DESC)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Title (A-Z)") },
                                onClick = {
                                    viewModel.setSortOrder(SortOrder.TITLE_ASC)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp
                        ),
                        cursorBrush = SolidColor(Brand1),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    "Search tasks...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.setSearchQuery("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            }

            // Filter Chips Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter is TaskFilter.All,
                        onClick = { viewModel.setFilter(TaskFilter.All) },
                        label = { Text("All") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter is TaskFilter.Overdue,
                        onClick = { viewModel.setFilter(TaskFilter.Overdue) },
                        label = { Text("⚠️ Overdue") }
                    )
                }
                item {
                    FilterChip(
                        selected = (selectedFilter as? TaskFilter.ByPriority)?.priority == Priority.HIGH,
                        onClick = { viewModel.setFilter(TaskFilter.ByPriority(Priority.HIGH)) },
                        label = { Text("🔴 High") }
                    )
                }
                items(availableCategories) { cat ->
                    FilterChip(
                        selected = (selectedFilter as? TaskFilter.ByCategory)?.category == cat,
                        onClick = { viewModel.setFilter(TaskFilter.ByCategory(cat)) },
                        label = { Text(cat) }
                    )
                }
            }

            // Inline Add Card
            AnimatedVisibility(
                visible = isAdding,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        BasicTextField(
                            value = draftText,
                            onValueChange = { draftText = it },
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            ),
                            cursorBrush = SolidColor(Brand1),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (draftText.isNotBlank()) {
                                    viewModel.insertItem(draftText)
                                    draftText = ""
                                    isAdding = false
                                }
                            }),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (draftText.isEmpty()) {
                                    Text(
                                        "What needs to be done?",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                                innerTextField()
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    val current = draftText
                                    draftText = ""
                                    isAdding = false
                                    onExpandAdd(current)
                                }
                            ) {
                                Text("More Options")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    draftText = ""
                                    isAdding = false
                                }
                            ) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (draftText.isNotBlank()) {
                                        viewModel.insertItem(draftText)
                                        draftText = ""
                                        isAdding = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Brand1)
                            ) {
                                Text("Add")
                            }
                        }
                    }
                }
            }

            // Task List
            if (sections.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Brand1,
                                modifier = Modifier
                                    .padding(18.dp)
                                    .fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tasks here!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap + or use quick add to create your first task.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    sections.forEach { section ->
                        when (section) {
                            is TodoListSection.Header -> {
                                item(key = section.key) {
                                    Text(
                                        text = section.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                                    )
                                }
                            }
                            is TodoListSection.Item -> {
                                val item = section.todo
                                item(key = item.id) {
                                    SharedTodoRow(
                                        item = item,
                                        isSelected = selectedTodoIds.contains(item.id),
                                        onDone = { viewModel.done(item) },
                                        onClick = {
                                            if (selectedTodoIds.isNotEmpty()) {
                                                viewModel.toggleSelection(item.id)
                                            } else {
                                                onItemClick(item)
                                            }
                                        },
                                        onLongClick = { viewModel.toggleSelection(item.id) },
                                        onPinToggle = { viewModel.togglePin(item) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SharedTodoRow(
    item: TodoItem,
    isSelected: Boolean,
    onDone: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPinToggle: () -> Unit
) {
    val priorityColor = when (item.priority) {
        Priority.HIGH -> HighPriorityColor
        Priority.MEDIUM -> MediumPriorityColor
        Priority.LOW -> LowPriorityColor
        Priority.NONE -> Color.Transparent
    }

    val now = DateTimeHelper.currentTimeMillis()
    val isOverdue = item.dueDate != null && item.dueDate < now

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface,
        border = if (isSelected) BorderStroke(1.5.dp, Brand1) else null,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority Strip / Dot
            if (item.priority != Priority.NONE) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 28.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(priorityColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }

            // Checkbox
            IconButton(
                onClick = onDone,
                modifier = Modifier.size(28.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                    color = Color.Transparent,
                    modifier = Modifier.size(22.dp)
                ) {}
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Title & Meta
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (item.description.isNotBlank()) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Badges row
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.isPinned) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                "📌 Pinned",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (item.category.isNotBlank() && item.category != "General") {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                item.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    val due = item.dueDate
                    if (due != null) {
                        val dueText = formatDueTimestamp(due)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isOverdue) HighPriorityColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                dueText,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOverdue) HighPriorityColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (item.subtasks.isNotEmpty()) {
                        val completedSubtasks = item.subtasks.count { it.isDone }
                        Text(
                            "☑ $completedSubtasks/${item.subtasks.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Pin toggle
            IconButton(
                onClick = onPinToggle,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.PushPin,
                    contentDescription = "Pin",
                    tint = if (item.isPinned) Brand1 else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun formatDueTimestamp(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val month = local.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    val minute = local.minute.toString().padStart(2, '0')
    val hour = local.hour
    val amPm = if (hour >= 12) "PM" else "AM"
    val displayHour = if (hour % 12 == 0) 12 else hour % 12
    return "📅 $month ${local.dayOfMonth} · $displayHour:$minute $amPm"
}
