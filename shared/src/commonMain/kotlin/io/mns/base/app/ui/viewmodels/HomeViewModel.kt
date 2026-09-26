package io.mns.base.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.mns.base.app.data.Priority
import io.mns.base.app.data.RepeatInterval
import io.mns.base.app.data.SortOrder
import io.mns.base.app.data.Subtask
import io.mns.base.app.data.TaskFilter
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoListSection
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.notifications.ReminderManager
import io.mns.base.app.util.DateTimeHelper
import io.mns.base.app.util.generateRandomUuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel(
    repository: TodoRepository? = null,
    reminderManager: ReminderManager? = null,
    @Suppress("UNUSED_PARAMETER") application: Any? = null
) : ViewModel(), KoinComponent {

    private val injectedRepo: TodoRepository by inject()
    private val injectedReminder: ReminderManager by inject()

    private val repo: TodoRepository = repository ?: injectedRepo
    private val reminder: ReminderManager = reminderManager ?: injectedReminder

    private val _addClicked = MutableStateFlow(false)
    val addClicked: StateFlow<Boolean> = _addClicked.asStateFlow()

    private val _settingClicked = MutableStateFlow(false)
    val settingClicked: StateFlow<Boolean> = _settingClicked.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow<TaskFilter>(TaskFilter.All)
    val selectedFilter: StateFlow<TaskFilter> = _selectedFilter.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.CREATION_DATE_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _availableTags = MutableStateFlow<List<String>>(emptyList())
    val availableTags: StateFlow<List<String>> = _availableTags.asStateFlow()

    private val _availableCategories = MutableStateFlow(listOf("General", "Work", "Personal", "Shopping"))
    val availableCategories: StateFlow<List<String>> = _availableCategories.asStateFlow()

    private val _selectedTodoIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedTodoIds: StateFlow<Set<String>> = _selectedTodoIds.asStateFlow()

    val rawTodos: StateFlow<List<TodoItem>> = repo.loadTodoItems()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val sections: StateFlow<List<TodoListSection>> = combine(
        rawTodos,
        _searchQuery,
        _selectedFilter,
        _sortOrder
    ) { todos, query, filter, sort ->
        buildSections(todos, query, filter, sort)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun toggleSelection(todoId: String) {
        val current = _selectedTodoIds.value
        _selectedTodoIds.value = if (current.contains(todoId)) current - todoId else current + todoId
    }

    fun selectAll() {
        val allIds = rawTodos.value.map { it.id }.toSet()
        _selectedTodoIds.value = allIds
    }

    fun clearSelection() {
        _selectedTodoIds.value = emptySet()
    }

    fun batchDone() {
        val selectedSet = _selectedTodoIds.value
        val items = rawTodos.value.filter { selectedSet.contains(it.id) }
        if (items.isEmpty()) return
        viewModelScope.launch {
            val nextItems = repo.batchDone(items)
            items.forEach { reminder.cancelReminder(it.id) }
            val now = DateTimeHelper.currentTimeMillis()
            nextItems.forEach { next ->
                if (next.dueDate != null && next.dueDate > now) {
                    reminder.scheduleReminder(next)
                }
            }
            clearSelection()
        }
    }

    fun batchDelete() {
        val selectedSet = _selectedTodoIds.value
        val items = rawTodos.value.filter { selectedSet.contains(it.id) }
        if (items.isEmpty()) return
        viewModelScope.launch {
            repo.batchDelete(items)
            items.forEach { reminder.cancelReminder(it.id) }
            clearSelection()
        }
    }

    fun batchTogglePin() {
        val selectedSet = _selectedTodoIds.value
        val items = rawTodos.value.filter { selectedSet.contains(it.id) }
        if (items.isEmpty()) return
        val shouldPin = items.any { !it.isPinned }
        viewModelScope.launch {
            repo.batchSetPin(items, shouldPin)
            clearSelection()
        }
    }

    fun togglePin(todo: TodoItem) {
        updateTodo(todo.copy(isPinned = !todo.isPinned))
    }

    private fun buildSections(
        items: List<TodoItem>,
        rawQuery: String,
        filter: TaskFilter,
        sort: SortOrder
    ): List<TodoListSection> {
        val tagsSet = mutableSetOf<String>()
        items.forEach { tagsSet.addAll(it.tags) }
        _availableTags.value = tagsSet.sorted()

        val defaultCategories = listOf("General", "Work", "Personal", "Shopping")
        val categorySet = mutableSetOf<String>().apply { addAll(defaultCategories) }
        items.forEach { if (it.category.isNotBlank()) categorySet.add(it.category) }
        _availableCategories.value = categorySet.toList()

        val query = rawQuery.trim()
        val now = DateTimeHelper.currentTimeMillis()

        val filtered = items.filter { item ->
            val matchesQuery = query.isEmpty() ||
                item.title.contains(query, ignoreCase = true) ||
                item.description.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                is TaskFilter.All -> true
                is TaskFilter.ByPriority -> item.priority == filter.priority
                is TaskFilter.ByTag -> item.tags.contains(filter.tag)
                is TaskFilter.ByCategory -> item.category.equals(filter.category, ignoreCase = true)
                is TaskFilter.Overdue -> item.dueDate != null && item.dueDate < now
            }

            matchesQuery && matchesFilter
        }

        val pinned = filtered.filter { it.isPinned }
        val unpinned = filtered.filter { !it.isPinned }

        val pinnedSection = if (pinned.isNotEmpty()) {
            listOf(TodoListSection.Header(-999L, "📌 Pinned")) + pinned.map { TodoListSection.Item(it) }
        } else {
            emptyList()
        }

        val unpinnedSections = when (sort) {
            SortOrder.CREATION_DATE_DESC -> groupByHour(unpinned)
            SortOrder.PRIORITY_DESC -> groupByPriority(unpinned)
            SortOrder.DUE_DATE_ASC -> groupByDueDate(unpinned)
            SortOrder.TITLE_ASC -> groupByAlphabet(unpinned)
        }

        return pinnedSection + unpinnedSections
    }

    fun insertItem(title: String) {
        insertTodo(title = title)
    }

    fun insertTodo(
        title: String,
        description: String = "",
        dueDate: Long? = null,
        priority: Priority = Priority.NONE,
        tags: List<String> = emptyList(),
        subtasks: List<Subtask> = emptyList(),
        repeatInterval: RepeatInterval = RepeatInterval.NONE,
        isPinned: Boolean = false,
        category: String = "General"
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val now = DateTimeHelper.currentTimeMillis()
            val item = TodoItem(
                id = generateRandomUuid(),
                createdAt = now,
                title = title.trim(),
                description = description.trim(),
                dueDate = dueDate,
                priority = priority,
                tags = tags,
                subtasks = subtasks,
                repeatInterval = repeatInterval,
                isPinned = isPinned,
                category = category
            )
            repo.insertTodoItem(item)
            if (item.dueDate != null && item.dueDate > now) {
                reminder.scheduleReminder(item)
            }
        }
    }

    fun updateTodo(todo: TodoItem) {
        viewModelScope.launch {
            repo.updateTodoItem(todo)
            val now = DateTimeHelper.currentTimeMillis()
            if (todo.dueDate != null && todo.dueDate > now) {
                reminder.scheduleReminder(todo)
            } else {
                reminder.cancelReminder(todo.id)
            }
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            repo.deleteTodoItem(todo)
            reminder.cancelReminder(todo.id)
        }
    }

    fun restoreTodo(todo: TodoItem) {
        viewModelScope.launch {
            repo.restoreTodoItem(todo)
            val now = DateTimeHelper.currentTimeMillis()
            if (todo.dueDate != null && todo.dueDate > now) {
                reminder.scheduleReminder(todo)
            }
        }
    }

    fun done(item: TodoItem) {
        viewModelScope.launch {
            val next = repo.done(item)
            reminder.cancelReminder(item.id)
            val now = DateTimeHelper.currentTimeMillis()
            if (next?.dueDate != null && next.dueDate > now) {
                reminder.scheduleReminder(next)
            }
        }
    }

    fun uncomplete(item: TodoItem) {
        viewModelScope.launch {
            val doneItem = repo.getDoneById(item.id)
            if (doneItem != null) {
                repo.uncomplete(doneItem)
            } else {
                repo.restoreTodoItem(item)
            }
            val now = DateTimeHelper.currentTimeMillis()
            if (item.dueDate != null && item.dueDate > now) {
                reminder.scheduleReminder(item)
            }
        }
    }

    fun addClicked() {
        _addClicked.value = true
    }

    fun addHandled() {
        _addClicked.value = false
    }

    fun settingClicked() {
        _settingClicked.value = true
    }

    fun settingHandled() {
        _settingClicked.value = false
    }

    private fun groupByHour(items: List<TodoItem>): List<TodoListSection> {
        if (items.isEmpty()) return emptyList()
        return items
            .groupBy { DateTimeHelper.truncateToHour(it.createdAt) }
            .entries
            .sortedByDescending { it.key }
            .flatMap { (hourStartMs, groupItems) ->
                listOf(TodoListSection.Header(hourStartMs, DateTimeHelper.formatHourRange(hourStartMs))) +
                    groupItems.map { TodoListSection.Item(it) }
            }
    }

    private fun groupByPriority(items: List<TodoItem>): List<TodoListSection> {
        if (items.isEmpty()) return emptyList()
        val priorities = listOf(Priority.HIGH, Priority.MEDIUM, Priority.LOW, Priority.NONE)
        return priorities.flatMap { p ->
            val matching = items.filter { it.priority == p }.sortedByDescending { it.createdAt }
            if (matching.isNotEmpty()) {
                val label = when (p) {
                    Priority.HIGH -> "🔴 High Priority"
                    Priority.MEDIUM -> "🟠 Medium Priority"
                    Priority.LOW -> "🟢 Low Priority"
                    Priority.NONE -> "⚪ No Priority"
                }
                listOf(TodoListSection.Header(p.level.toLong(), label)) + matching.map { TodoListSection.Item(it) }
            } else {
                emptyList()
            }
        }
    }

    private fun groupByDueDate(items: List<TodoItem>): List<TodoListSection> {
        if (items.isEmpty()) return emptyList()
        val now = DateTimeHelper.currentTimeMillis()
        val overdue = items.filter { it.dueDate != null && it.dueDate < now }.sortedBy { it.dueDate }
        val todayStart = DateTimeHelper.startOfToday()
        val todayEnd = DateTimeHelper.endOfToday()
        val today = items.filter { it.dueDate != null && it.dueDate in now until todayEnd }.sortedBy { it.dueDate }
        val upcoming = items.filter { it.dueDate != null && it.dueDate >= todayEnd }.sortedBy { it.dueDate }
        val noDueDate = items.filter { it.dueDate == null }.sortedByDescending { it.createdAt }

        val list = mutableListOf<TodoListSection>()
        if (overdue.isNotEmpty()) {
            list.add(TodoListSection.Header(1L, "⚠️ Overdue"))
            list.addAll(overdue.map { TodoListSection.Item(it) })
        }
        if (today.isNotEmpty()) {
            list.add(TodoListSection.Header(2L, "📅 Due Today"))
            list.addAll(today.map { TodoListSection.Item(it) })
        }
        if (upcoming.isNotEmpty()) {
            list.add(TodoListSection.Header(3L, "⏳ Upcoming"))
            list.addAll(upcoming.map { TodoListSection.Item(it) })
        }
        if (noDueDate.isNotEmpty()) {
            list.add(TodoListSection.Header(4L, "📋 No Due Date"))
            list.addAll(noDueDate.map { TodoListSection.Item(it) })
        }
        return list
    }

    private fun groupByAlphabet(items: List<TodoItem>): List<TodoListSection> {
        if (items.isEmpty()) return emptyList()
        return items
            .sortedBy { it.title.lowercase() }
            .groupBy { it.title.firstOrNull()?.uppercaseChar() ?: '#' }
            .flatMap { (char, group) ->
                listOf(TodoListSection.Header(char.code.toLong(), char.toString())) +
                    group.map { TodoListSection.Item(it) }
            }
    }
}
