package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val repository: TodoRepository by inject()
    private val reminderManager: ReminderManager by inject()

    private val _addClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val addClicked: LiveData<Boolean>
        get() = _addClicked

    private val _settingClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val settingClicked: LiveData<Boolean>
        get() = _settingClicked

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow<TaskFilter>(TaskFilter.All)
    val selectedFilter: StateFlow<TaskFilter> = _selectedFilter.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.CREATION_DATE_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _availableTags = MutableStateFlow<List<String>>(emptyList())
    val availableTags: StateFlow<List<String>> = _availableTags.asStateFlow()

    private val _availableCategories = MutableStateFlow<List<String>>(listOf("General", "Work", "Personal", "Shopping"))
    val availableCategories: StateFlow<List<String>> = _availableCategories.asStateFlow()

    private val _selectedTodoIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedTodoIds: StateFlow<Set<String>> = _selectedTodoIds.asStateFlow()

    val rawTodos: LiveData<List<TodoItem>> = repository.loadTodoItems()

    private val _sections = androidx.lifecycle.MediatorLiveData<List<TodoListSection>>()
    val sections: LiveData<List<TodoListSection>> = _sections

    init {
        _sections.addSource(rawTodos) { updateFilteredSections() }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        updateFilteredSections()
    }

    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
        updateFilteredSections()
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        updateFilteredSections()
    }

    fun toggleSelection(todoId: String) {
        val current = _selectedTodoIds.value
        _selectedTodoIds.value = if (current.contains(todoId)) current - todoId else current + todoId
    }

    fun selectAll() {
        val allIds = rawTodos.value?.map { it.id }?.toSet() ?: emptySet()
        _selectedTodoIds.value = allIds
    }

    fun clearSelection() {
        _selectedTodoIds.value = emptySet()
    }

    fun batchDone() {
        val selectedSet = _selectedTodoIds.value
        val items = rawTodos.value?.filter { selectedSet.contains(it.id) } ?: emptyList()
        if (items.isEmpty()) return
        viewModelScope.launch {
            val nextItems = repository.batchDone(items)
            items.forEach { reminderManager.cancelReminder(it.id) }
            nextItems.forEach { next ->
                if (next.dueDate != null && next.dueDate > System.currentTimeMillis()) {
                    reminderManager.scheduleReminder(next)
                }
            }
            clearSelection()
        }
    }

    fun batchDelete() {
        val selectedSet = _selectedTodoIds.value
        val items = rawTodos.value?.filter { selectedSet.contains(it.id) } ?: emptyList()
        if (items.isEmpty()) return
        viewModelScope.launch {
            repository.batchDelete(items)
            items.forEach { reminderManager.cancelReminder(it.id) }
            clearSelection()
        }
    }

    fun batchTogglePin() {
        val selectedSet = _selectedTodoIds.value
        val items = rawTodos.value?.filter { selectedSet.contains(it.id) } ?: emptyList()
        if (items.isEmpty()) return
        val shouldPin = items.any { !it.isPinned }
        viewModelScope.launch {
            repository.batchSetPin(items, shouldPin)
            clearSelection()
        }
    }

    fun togglePin(todo: TodoItem) {
        updateTodo(todo.copy(isPinned = !todo.isPinned))
    }

    private fun updateFilteredSections() {
        val items = rawTodos.value ?: emptyList()

        // Extract available unique tags
        val tagsSet = mutableSetOf<String>()
        items.forEach { tagsSet.addAll(it.tags) }
        _availableTags.value = tagsSet.sorted()

        // Extract available unique categories
        val defaultCategories = listOf("General", "Work", "Personal", "Shopping")
        val categorySet = mutableSetOf<String>().apply { addAll(defaultCategories) }
        items.forEach { if (it.category.isNotBlank()) categorySet.add(it.category) }
        _availableCategories.value = categorySet.toList()

        val query = _searchQuery.value.trim()
        val filter = _selectedFilter.value
        val sort = _sortOrder.value
        val now = System.currentTimeMillis()

        // Filter
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

        // Pinned tasks separation: Pinned tasks stay fixed at the top
        val pinned = filtered.filter { it.isPinned }
        val unpinned = filtered.filter { !it.isPinned }

        val pinnedSection = if (pinned.isNotEmpty()) {
            listOf(TodoListSection.Header(-999L, "📌 Pinned")) + pinned.map { TodoListSection.Item(it) }
        } else {
            emptyList()
        }

        // Sort and Section remaining unpinned items
        val unpinnedSections = when (sort) {
            SortOrder.CREATION_DATE_DESC -> groupByHour(unpinned)
            SortOrder.PRIORITY_DESC -> groupByPriority(unpinned)
            SortOrder.DUE_DATE_ASC -> groupByDueDate(unpinned)
            SortOrder.TITLE_ASC -> groupByAlphabet(unpinned)
        }

        _sections.value = pinnedSection + unpinnedSections
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
            val item = TodoItem(
                id = UUID.randomUUID().toString(),
                createdAt = System.currentTimeMillis(),
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
            repository.insertTodoItem(item)
            if (item.dueDate != null && item.dueDate > System.currentTimeMillis()) {
                reminderManager.scheduleReminder(item)
            }
        }
    }

    fun updateTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.updateTodoItem(todo)
            if (todo.dueDate != null && todo.dueDate > System.currentTimeMillis()) {
                reminderManager.scheduleReminder(todo)
            } else {
                reminderManager.cancelReminder(todo.id)
            }
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.deleteTodoItem(todo)
            reminderManager.cancelReminder(todo.id)
        }
    }

    fun restoreTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.restoreTodoItem(todo)
            if (todo.dueDate != null && todo.dueDate > System.currentTimeMillis()) {
                reminderManager.scheduleReminder(todo)
            }
        }
    }

    fun done(item: TodoItem) {
        viewModelScope.launch {
            val next = repository.done(item)
            reminderManager.cancelReminder(item.id)
            if (next?.dueDate != null && next.dueDate > System.currentTimeMillis()) {
                reminderManager.scheduleReminder(next)
            }
        }
    }

    fun uncomplete(item: TodoItem) {
        viewModelScope.launch {
            val doneItem = repository.getDoneById(item.id)
            if (doneItem != null) {
                repository.uncomplete(doneItem)
            } else {
                repository.restoreTodoItem(item)
            }
            if (item.dueDate != null && item.dueDate > System.currentTimeMillis()) {
                reminderManager.scheduleReminder(item)
            }
        }
    }

    fun addClicked() {
        _addClicked.postValue(true)
    }

    fun addHandled() {
        _addClicked.postValue(false)
    }

    fun settingClicked() {
        _settingClicked.postValue(true)
    }

    fun settingHandled() {
        _settingClicked.postValue(false)
    }

    private fun groupByHour(items: List<TodoItem>): List<TodoListSection> {
        if (items.isEmpty()) return emptyList()
        return items
            .groupBy { truncateToHour(it.createdAt) }
            .entries
            .sortedByDescending { it.key }
            .flatMap { (hourStartMs, groupItems) ->
                listOf(TodoListSection.Header(hourStartMs, formatHourLabel(hourStartMs))) +
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
        val now = System.currentTimeMillis()
        val overdue = items.filter { it.dueDate != null && it.dueDate < now }.sortedBy { it.dueDate }
        val todayStart = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.timeInMillis
        val todayEnd = todayStart + 86_400_000L
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
            .sortedBy { it.title.lowercase(Locale.getDefault()) }
            .groupBy { it.title.firstOrNull()?.uppercaseChar() ?: '#' }
            .flatMap { (char, group) ->
                listOf(TodoListSection.Header(char.code.toLong(), char.toString())) +
                    group.map { TodoListSection.Item(it) }
            }
    }

    private fun truncateToHour(timeMs: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMs
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun formatHourLabel(hourStartMs: Long): String {
        val now = Calendar.getInstance()
        val hour = Calendar.getInstance().apply { timeInMillis = hourStartMs }

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val start = timeFormat.format(Date(hourStartMs))
        val end = timeFormat.format(Date(hourStartMs + 3_600_000L))
        val timeRange = "$start – $end"

        val isToday = now.get(Calendar.YEAR) == hour.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == hour.get(Calendar.DAY_OF_YEAR)

        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val isYesterday = yesterday.get(Calendar.YEAR) == hour.get(Calendar.YEAR) &&
            yesterday.get(Calendar.DAY_OF_YEAR) == hour.get(Calendar.DAY_OF_YEAR)

        return when {
            isToday -> "Today · $timeRange"
            isYesterday -> "Yesterday · $timeRange"
            else -> SimpleDateFormat("EEE, MMM d · ", Locale.getDefault())
                .format(Date(hourStartMs)) + timeRange
        }
    }
}
