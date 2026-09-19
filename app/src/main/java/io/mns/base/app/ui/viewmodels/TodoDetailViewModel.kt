package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.Priority
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import java.util.UUID
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

import io.mns.base.app.data.Subtask
import io.mns.base.app.notifications.ReminderManager

class TodoDetailViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val repository: TodoRepository by inject()
    private val reminderManager: ReminderManager by inject()

    private val _todoItem = MutableLiveData<TodoItem?>()
    val todoItem: LiveData<TodoItem?> get() = _todoItem

    private val _doneItem = MutableLiveData<DoneItem?>()
    val doneItem: LiveData<DoneItem?> get() = _doneItem

    fun loadTodo(id: String) {
        viewModelScope.launch {
            _todoItem.value = repository.getTodoById(id)
        }
    }

    fun loadDone(id: String) {
        viewModelScope.launch {
            _doneItem.value = repository.getDoneById(id)
        }
    }

    fun saveTodo(
        id: String?,
        title: String,
        description: String,
        dueDate: Long?,
        priority: Priority,
        tags: List<String>,
        subtasks: List<Subtask> = emptyList(),
        onComplete: () -> Unit
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val existing = id?.let { repository.getTodoById(it) }
            val itemToSchedule = if (existing != null) {
                val updated = existing.copy(
                    title = title.trim(),
                    description = description.trim(),
                    dueDate = dueDate,
                    priority = priority,
                    tags = tags,
                    subtasks = subtasks
                )
                repository.updateTodoItem(updated)
                updated
            } else {
                val inserted = TodoItem(
                    id = id ?: UUID.randomUUID().toString(),
                    createdAt = System.currentTimeMillis(),
                    title = title.trim(),
                    description = description.trim(),
                    dueDate = dueDate,
                    priority = priority,
                    tags = tags,
                    subtasks = subtasks
                )
                repository.insertTodoItem(inserted)
                inserted
            }

            if (itemToSchedule.dueDate != null && itemToSchedule.dueDate > System.currentTimeMillis()) {
                reminderManager.scheduleReminder(itemToSchedule)
            } else {
                reminderManager.cancelReminder(itemToSchedule.id)
            }

            onComplete()
        }
    }

    fun completeTodo(todo: TodoItem, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.done(todo)
            onComplete()
        }
    }

    fun deleteTodo(todo: TodoItem, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteTodoItem(todo)
            onComplete()
        }
    }

    fun deleteDone(item: DoneItem, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteDoneItem(item)
            onComplete()
        }
    }
}
