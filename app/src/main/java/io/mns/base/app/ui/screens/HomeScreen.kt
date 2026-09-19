package io.mns.base.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mns.base.app.R
import io.mns.base.app.data.Priority
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoListSection
import io.mns.base.app.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import io.mns.base.app.data.SortOrder
import io.mns.base.app.data.TaskFilter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private val Brand1 = Color(0xFF6366F1)
private val Brand2 = Color(0xFFA78BFA)

private fun brandBrush(
    start: Offset = Offset.Zero,
    end: Offset = Offset.Infinite
) = Brush.linearGradient(colors = listOf(Brand1, Brand2), start = start, end = end)

private fun formatDueDate(dueDateMs: Long): Pair<String, Boolean> {
    val now = Calendar.getInstance()
    val due = Calendar.getInstance().apply { timeInMillis = dueDateMs }
    val isOverdue = dueDateMs < System.currentTimeMillis()
    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(dueDateMs))
    val dayDiff = due.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR)
    val yearDiff = due.get(Calendar.YEAR) - now.get(Calendar.YEAR)
    val text = when {
        yearDiff == 0 && dayDiff == 0 -> "Today $timeStr"
        yearDiff == 0 && dayDiff == 1 -> "Tomorrow $timeStr"
        yearDiff == 0 && dayDiff == -1 -> "Yesterday $timeStr"
        else -> "${SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(dueDateMs))} $timeStr"
    }
    return (if (isOverdue) "Overdue · $text" else text) to isOverdue
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit,
    onItemClick: (TodoItem) -> Unit = {},
    onExpandAdd: (draft: String) -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val sections by viewModel.sections.observeAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val availableTags by viewModel.availableTags.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    HomeScreenContent(
        sections = sections,
        searchQuery = searchQuery,
        selectedFilter = selectedFilter,
        sortOrder = sortOrder,
        availableTags = availableTags,
        onSettingsClick = onSettingsClick,
        onItemClick = onItemClick,
        onExpandAdd = onExpandAdd,
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        onFilterChange = { viewModel.setFilter(it) },
        onSortOrderChange = { viewModel.setSortOrder(it) },
        onDone = { item ->
            viewModel.done(item)
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Task completed",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.uncomplete(item)
                }
            }
        },
        onDelete = { item ->
            viewModel.deleteTodo(item)
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Task deleted",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.restoreTodo(item)
                }
            }
        },
        onAdd = { viewModel.insertItem(it) },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    sections: List<TodoListSection>,
    searchQuery: String = "",
    selectedFilter: TaskFilter = TaskFilter.All,
    sortOrder: SortOrder = SortOrder.CREATION_DATE_DESC,
    availableTags: List<String> = emptyList(),
    onSettingsClick: () -> Unit = {},
    onItemClick: (TodoItem) -> Unit = {},
    onExpandAdd: (draft: String) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onFilterChange: (TaskFilter) -> Unit = {},
    onSortOrderChange: (SortOrder) -> Unit = {},
    onDone: (TodoItem) -> Unit = {},
    onDelete: (TodoItem) -> Unit = {},
    onAdd: (String) -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    initialFabVisible: Boolean = true,
    initialIsAdding: Boolean = false
) {
    var isAdding by remember { mutableStateOf(initialIsAdding) }
    var draftText by remember { mutableStateOf("") }
    var fabVisible by remember { mutableStateOf(initialFabVisible) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { fabVisible = true }

    val commitOrDismiss = {
        val trimmed = draftText.trim()
        if (trimmed.isNotBlank()) {
            onAdd(trimmed)
            draftText = ""
        }
        isAdding = false
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.to_be_done),
                        style = TextStyle(
                            brush = brandBrush(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (isSearchVisible || searchQuery.isNotEmpty()) Brand1 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = order.label,
                                            fontWeight = if (order == sortOrder) FontWeight.Bold else FontWeight.Normal,
                                            color = if (order == sortOrder) Brand1 else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        onSortOrderChange(order)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible && !isAdding,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    initialScale = 0.5f
                ) + fadeIn(tween(300))
            ) {
                GradientFab(onClick = { isAdding = true })
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clickable(
                    enabled = isAdding,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    commitOrDismiss()
                }
        ) {
            BackgroundOrbs()

            Column(modifier = Modifier.fillMaxSize()) {
                // Animated Search Bar
                AnimatedVisibility(visible = isSearchVisible) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        placeholder = { Text("Search tasks...", style = MaterialTheme.typography.bodyMedium) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    )
                }

                // Filter Chips Carousel
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter is TaskFilter.All,
                            onClick = { onFilterChange(TaskFilter.All) },
                            label = { Text("All") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter is TaskFilter.ByPriority && (selectedFilter as TaskFilter.ByPriority).priority == Priority.HIGH,
                            onClick = { onFilterChange(TaskFilter.ByPriority(Priority.HIGH)) },
                            label = { Text("🔴 High") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter is TaskFilter.ByPriority && (selectedFilter as TaskFilter.ByPriority).priority == Priority.MEDIUM,
                            onClick = { onFilterChange(TaskFilter.ByPriority(Priority.MEDIUM)) },
                            label = { Text("🟠 Med") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter is TaskFilter.ByPriority && (selectedFilter as TaskFilter.ByPriority).priority == Priority.LOW,
                            onClick = { onFilterChange(TaskFilter.ByPriority(Priority.LOW)) },
                            label = { Text("🟢 Low") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter is TaskFilter.Overdue,
                            onClick = { onFilterChange(TaskFilter.Overdue) },
                            label = { Text("⚠️ Overdue") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    items(availableTags) { tag ->
                        FilterChip(
                            selected = selectedFilter is TaskFilter.ByTag && (selectedFilter as TaskFilter.ByTag).tag == tag,
                            onClick = { onFilterChange(TaskFilter.ByTag(tag)) },
                            label = { Text("#$tag") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                val isFilterActive = searchQuery.isNotEmpty() || selectedFilter !is TaskFilter.All

                if (sections.isEmpty() && !isAdding) {
                    if (isFilterActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "No tasks match your filter",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                OutlinedButton(
                                    onClick = {
                                        onSearchQueryChange("")
                                        onFilterChange(TaskFilter.All)
                                    }
                                ) {
                                    Text("Reset Filters")
                                }
                            }
                        }
                    } else {
                        EmptyState(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp, top = 4.dp)
                    ) {
                        if (isAdding) {
                            item(key = "inline_add_card") {
                                InlineAddTodoCard(
                                    draftText = draftText,
                                    onDraftChange = { draftText = it },
                                    onSubmit = commitOrDismiss,
                                    onCancel = {
                                        draftText = ""
                                        isAdding = false
                                    },
                                    onExpand = {
                                        val currentDraft = draftText.trim()
                                        draftText = ""
                                        isAdding = false
                                        onExpandAdd(currentDraft)
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                        .animateItem()
                                )
                            }
                        }

                        sections.forEach { section ->
                            when (section) {
                                is TodoListSection.Header -> stickyHeader(key = section.key) {
                                    TimeSegmentHeader(label = section.label)
                                }
                                is TodoListSection.Item -> {
                                    item(key = section.todo.id) {
                                        SwipeableTodoItemRow(
                                            item = section.todo,
                                            onDone = { onDone(section.todo) },
                                            onDelete = { onDelete(section.todo) },
                                            onClick = { onItemClick(section.todo) },
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp, vertical = 5.dp)
                                                .animateItem(
                                                    placementSpec = spring(
                                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                                        stiffness = Spring.StiffnessLow
                                                    ),
                                                    fadeOutSpec = tween(220)
                                                )
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
}



@Composable
private fun BackgroundOrbs() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 90.dp, y = (-70).dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Brand1.copy(alpha = 0.09f), Color.Transparent)
                    ),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = (-70).dp, y = 30.dp)
                .align(Alignment.BottomStart)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Brand2.copy(alpha = 0.07f), Color.Transparent)
                    ),
                    CircleShape
                )
        )
    }
}

@Composable
fun GradientFab(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fabScale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .size(62.dp)
            .shadow(
                elevation = if (pressed) 6.dp else 14.dp,
                shape = CircleShape,
                spotColor = Brand1.copy(alpha = 0.4f)
            )
            .clip(CircleShape)
            .background(brandBrush())
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun TimeSegmentHeader(label: String) {
    val transition = rememberInfiniteTransition(label = "headerShimmer")
    val shimmerPos by transition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerPos"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Brand1.copy(alpha = 0.2f + shimmerPos.coerceIn(0f, 0.3f)),
                            Brand2.copy(alpha = 0.4f)
                        )
                    )
                )
        )
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.4.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Brand2.copy(alpha = 0.4f),
                            Brand1.copy(alpha = 0.2f + shimmerPos.coerceIn(0f, 0.3f)),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTodoItemRow(
    item: TodoItem,
    onDone: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onDone()
                    true
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Brand1.copy(alpha = 0.85f)
                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
                else -> Color.Transparent
            }
            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Done
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.DeleteOutline
                else -> null
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        },
        modifier = modifier
    ) {
        TodoItemRow(
            item = item,
            onDone = onDone,
            onClick = onClick
        )
    }
}

@Composable
fun TodoItemRow(
    item: TodoItem,
    onDone: () -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val btnInteraction = remember { MutableInteractionSource() }
    val btnPressed by btnInteraction.collectIsPressedAsState()
    var checked by remember { mutableStateOf(false) }

    // Press-scale: only active before checked
    val btnScale by animateFloatAsState(
        targetValue = if (btnPressed && !checked) 0.80f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "btnScale"
    )

    // Fill circle scales in when checked
    val fillScale by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fillScale"
    )

    // Checkmark fades in after fill
    val checkAlpha by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(180, delayMillis = 100),
        label = "checkAlpha"
    )

    // Trigger onDone after the fill animation completes
    LaunchedEffect(checked) {
        if (checked) {
            delay(380)
            onDone()
        }
    }

    val stripeColor = if (item.priority != Priority.NONE) item.priority.color else Brand1

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Brand1.copy(alpha = 0.10f),
                ambientColor = Brand1.copy(alpha = 0.05f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(64.dp)
                    .background(
                        if (item.priority != Priority.NONE) Brush.linearGradient(listOf(stripeColor, stripeColor))
                        else brandBrush(start = Offset(0f, 0f), end = Offset(0f, Float.POSITIVE_INFINITY)),
                        RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
                    )
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp, end = 8.dp, top = 10.dp, bottom = 10.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.dueDate != null || item.priority != Priority.NONE || item.tags.isNotEmpty() || item.subtasks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.priority != Priority.NONE) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(item.priority.color, CircleShape)
                            )
                            Text(
                                text = item.priority.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = item.priority.color
                            )
                        }
                        if (item.dueDate != null) {
                            val (dueText, isOverdue) = formatDueDate(item.dueDate!!)
                            Text(
                                text = dueText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (item.subtasks.isNotEmpty()) {
                            val completed = item.subtasks.count { it.isDone }
                            val total = item.subtasks.size
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(10.dp),
                                        tint = if (completed == total) Brand1 else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "$completed/$total",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        val maxTags = if (item.dueDate != null) 1 else 2
                        item.tags.take(maxTags).forEach { tag ->
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall,
                                color = Brand1.copy(alpha = 0.85f),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .graphicsLayer { scaleX = btnScale; scaleY = btnScale }
                    .size(30.dp)
                    .clip(CircleShape)
                    .border(width = 1.5.dp, brush = brandBrush(), shape = CircleShape)
                    .clickable(
                        interactionSource = btnInteraction,
                        indication = ripple(bounded = true),
                        enabled = !checked,
                        onClick = { checked = true }
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Gradient fill that scales in
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .graphicsLayer { scaleX = fillScale; scaleY = fillScale }
                        .clip(CircleShape)
                        .background(brandBrush())
                )
                // Checkmark fades in on top
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = checkAlpha),
                    modifier = Modifier.size(16.dp)
                )
                // Empty dot shown when unchecked
                if (!checked) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(tween(400)) + scaleIn(
            initialScale = 0.85f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .shadow(elevation = 12.dp, shape = CircleShape, spotColor = Brand1.copy(alpha = 0.3f))
                    .background(brandBrush(), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.no_todo),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Tap + to add something",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddTodoDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        AddTodoDialogCard(
            text = text,
            onTextChange = { text = it },
            onDismiss = onDismiss,
            onAdd = onAdd
        )
    }
}

@Composable
fun AddTodoDialogCard(
    text: String,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    val enabled = text.isNotBlank()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "New Todo",
                style = TextStyle(
                    brush = brandBrush(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("What needs to be done?") },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Brand1,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = true
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (enabled) brandBrush()
                            else Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.outlineVariant,
                                    MaterialTheme.colorScheme.outlineVariant
                                )
                            )
                        )
                        .clickable(
                            enabled = enabled,
                            onClick = { onAdd(text) }
                        )
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Add",
                        color = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun InlineAddTodoCard(
    draftText: String,
    onDraftChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Brand1.copy(alpha = 0.25f),
                ambientColor = Brand1.copy(alpha = 0.10f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, Brand1, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(54.dp)
                    .background(brandBrush(), RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = draftText,
                onValueChange = onDraftChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                decorationBox = { innerTextField ->
                    if (draftText.isEmpty()) {
                        Text(
                            text = "What needs to be done?",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                }
            )

            // Expand to full details button
            IconButton(
                onClick = onExpand,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInFull,
                    contentDescription = "Open full details",
                    tint = Brand1,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Submit checkmark button
            IconButton(
                onClick = onSubmit,
                enabled = draftText.isNotBlank(),
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (draftText.isNotBlank()) Brand1 else Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Add todo",
                    tint = if (draftText.isNotBlank()) Color.White else MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Cancel button
            IconButton(
                onClick = onCancel,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

