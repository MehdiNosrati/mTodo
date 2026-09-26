package io.mns.base.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.SortOrder
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.data.backup.BackupManager
import io.mns.base.app.data.settings.AppSettings
import io.mns.base.app.notifications.ReminderManager
import io.mns.base.app.util.DateTimeHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingViewModel(
    repository: TodoRepository? = null,
    reminderManager: ReminderManager? = null,
    backupManager: BackupManager? = null,
    settings: AppSettings? = null,
    @Suppress("UNUSED_PARAMETER") application: Any? = null
) : ViewModel(), KoinComponent {

    private val injectedRepo: TodoRepository by inject()
    private val injectedReminder: ReminderManager by inject()
    private val injectedBackup: BackupManager by inject()
    private val injectedSettings: AppSettings by inject()

    private val repo: TodoRepository = repository ?: injectedRepo
    private val reminder: ReminderManager = reminderManager ?: injectedReminder
    val backup: BackupManager = backupManager ?: injectedBackup
    private val appSettings: AppSettings = settings ?: injectedSettings

    private val _sortOrder = MutableStateFlow(appSettings.getSortOrder())
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _dailyGoal = MutableStateFlow(appSettings.getDailyGoal())
    val dailyGoal: StateFlow<Int> = _dailyGoal.asStateFlow()

    val trashedTodos: StateFlow<List<TodoItem>> = repo.loadTrashedTodos()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val trashedDoneItems: StateFlow<List<DoneItem>> = repo.loadTrashedDoneItems()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _backClicked = MutableStateFlow(false)
    val backClicked: StateFlow<Boolean> = _backClicked.asStateFlow()

    private val _toggleTheme = MutableStateFlow(false)
    val toggleTheme: StateFlow<Boolean> = _toggleTheme.asStateFlow()

    init {
        viewModelScope.launch {
            repo.purgeOldTrash(30)
        }
    }

    fun areNotificationsEnabled(): Boolean = reminder.areNotificationsEnabled()

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        appSettings.setSortOrder(order)
    }

    fun setDailyGoal(goal: Int) {
        _dailyGoal.value = goal
        appSettings.setDailyGoal(goal)
    }

    fun restoreTodoFromTrash(todo: TodoItem) {
        viewModelScope.launch {
            repo.restoreTodoItem(todo)
            val now = DateTimeHelper.currentTimeMillis()
            if (todo.dueDate != null && todo.dueDate > now) {
                reminder.scheduleReminder(todo)
            }
        }
    }

    fun restoreDoneFromTrash(item: DoneItem) {
        viewModelScope.launch {
            repo.restoreDoneItem(item)
        }
    }

    fun hardDeleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            repo.hardDeleteTodo(todo.id)
        }
    }

    fun hardDeleteDone(item: DoneItem) {
        viewModelScope.launch {
            repo.hardDeleteDone(item.id)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            repo.emptyTrash()
        }
    }

    fun backClicked() {
        _backClicked.value = true
    }

    fun backHandled() {
        _backClicked.value = false
    }

    fun toggleTheme() {
        _toggleTheme.value = true
    }

    fun themeToggled() {
        _toggleTheme.value = false
    }
}
