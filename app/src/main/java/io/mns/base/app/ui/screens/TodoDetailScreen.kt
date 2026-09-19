package io.mns.base.app.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notes
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.Priority
import io.mns.base.app.data.TodoItem
import io.mns.base.app.ui.viewmodels.TodoDetailViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import org.koin.androidx.compose.koinViewModel

private val Brand1 = Color(0xFF6366F1)
private val Brand2 = Color(0xFFA78BFA)
private val DoneGreen = Color(0xFF10B981)
private val DoneTeal = Color(0xFF2DD4BF)

private fun brandBrush() = Brush.linearGradient(
    colors = listOf(Brand1, Brand2),
    start = Offset.Zero,
    end = Offset.Infinite
)

private val SUGGESTED_TAGS = listOf("Work", "Personal", "Urgent", "Shopping", "Health", "Study")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    id: String?,
    mode: String,
    draftTitle: String? = null,
    onBack: () -> Unit,
    viewModel: TodoDetailViewModel = koinViewModel()
) {
    val todoItem by viewModel.todoItem.observeAsState()
    val doneItem by viewModel.doneItem.observeAsState()

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
        onSave = { title, desc, dueDate, priority, tags ->
            viewModel.saveTodo(
                id = id,
                title = title,
                description = desc,
                dueDate = dueDate,
                priority = priority,
                tags = tags,
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
    onSave: (title: String, desc: String, dueDate: Long?, priority: Priority, tags: List<String>) -> Unit = { _, _, _, _, _ -> },
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

    // Populate state once data is loaded
    LaunchedEffect(todoItem) {
        todoItem?.let {
            title = it.title
            description = it.description
            priority = it.priority
            dueDate = it.dueDate
            tags = it.tags
        }
    }

    LaunchedEffect(doneItem) {
        doneItem?.let {
            title = it.title
            description = it.description
            priority = it.priority
            dueDate = it.dueDate
            tags = it.tags
        }
    }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    fun showDateTimePicker() {
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
                                    onSave(title, description, dueDate, priority, tags)
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
                                    onSave(title, description, dueDate, priority, tags)
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
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Task Title Input / Display
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    if (isReadOnly) {
                        Text(
                            text = title.ifBlank { "Untitled Task" },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    } else {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("What needs to be done?") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Brand1,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
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
                                placeholder = { Text("Add details, notes, or subtasks...") },
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

            // Due Date & Time Section
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

                    // Suggested tags & Custom tag input (if not read only)
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
                                onClick = { onSave(title, description, dueDate, priority, tags) }
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
                            onClick = { onSave(title, description, dueDate, priority, tags) }
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
