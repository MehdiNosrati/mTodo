package io.mns.base.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.Priority
import io.mns.base.app.data.RepeatInterval
import io.mns.base.app.data.Subtask
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.notifications.ReminderManager
import io.mns.base.app.util.DateTimeHelper
import io.mns.base.app.util.generateRandomUuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodoDetailViewModel(
    repository: TodoRepository? = null,
    reminderManager: ReminderManager? = null,
    @Suppress("UNUSED_PARAMETER") application: Any? = null
) : ViewModel(), KoinComponent {

    private val injectedRepo: TodoRepository by inject()
    private val injectedReminder: ReminderManager by inject()

    private val repo: TodoRepository = repository ?: injectedRepo
    private val reminder: ReminderManager = reminderManager ?: injectedReminder

    private val _todoItem = MutableStateFlow<TodoItem?>(null)
    val todoItem: StateFlow<TodoItem?> = _todoItem.asStateFlow()

    private val _doneItem = MutableStateFlow<DoneItem?>(null)
    val doneItem: StateFlow<DoneItem?> = _doneItem.asStateFlow()

    fun areNotificationsEnabled(): Boolean = reminder.areNotificationsEnabled()

    fun loadTodo(id: String) {
        viewModelScope.launch {
            _todoItem.value = repo.getTodoById(id)
        }
    }

    fun loadDone(id: String) {
        viewModelScope.launch {
            _doneItem.value = repo.getDoneById(id)
        }
    }

    fun saveTodo(
        id: String?,
        title: String,
        description: String,
        dueDate: Long?,
        priority: Priority,
        tags: List<String> = emptyList(),
        subtasks: List<Subtask> = emptyList(),
        repeatInterval: RepeatInterval = RepeatInterval.NONE,
        isPinned: Boolean = false,
        category: String = "General",
        onComplete: () -> Unit
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val now = DateTimeHelper.currentTimeMillis()
            val existing = id?.let { repo.getTodoById(it) }
            val itemToSchedule = if (existing != null) {
                val updated = existing.copy(
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
                repo.updateTodoItem(updated)
                updated
            } else {
                val inserted = TodoItem(
                    id = id ?: generateRandomUuid(),
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
                repo.insertTodoItem(inserted)
                inserted
            }

            if (itemToSchedule.dueDate != null && itemToSchedule.dueDate > now) {
                reminder.scheduleReminder(itemToSchedule)
            } else {
                reminder.cancelReminder(itemToSchedule.id)
            }

            onComplete()
        }
    }

    fun completeTodo(todo: TodoItem, onComplete: () -> Unit) {
        viewModelScope.launch {
            val next = repo.done(todo)
            reminder.cancelReminder(todo.id)
            val now = DateTimeHelper.currentTimeMillis()
            if (next?.dueDate != null && next.dueDate > now) {
                reminder.scheduleReminder(next)
            }
            onComplete()
        }
    }

    fun deleteTodo(todo: TodoItem, onComplete: () -> Unit) {
        viewModelScope.launch {
            repo.deleteTodoItem(todo)
            reminder.cancelReminder(todo.id)
            onComplete()
        }
    }

    fun deleteDone(item: DoneItem, onComplete: () -> Unit) {
        viewModelScope.launch {
            repo.deleteDoneItem(item)
            onComplete()
        }
    }
}
