package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoListSection
import io.mns.base.app.data.TodoRepository
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val repository: TodoRepository by inject()

    private val _addClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val addClicked: LiveData<Boolean>
        get() = _addClicked

    private val _settingClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val settingClicked: LiveData<Boolean>
        get() = _settingClicked

    val sections: LiveData<List<TodoListSection>> = repository.loadTodoItems().map { items ->
        groupByHour(items)
    }

    fun insertItem(title: String) {
        viewModelScope.launch {
            repository.insertTodoItem(TodoItem(UUID.randomUUID().toString(), Date().time, title))
        }
    }

    fun done(item: TodoItem) {
        viewModelScope.launch {
            repository.done(item)
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
