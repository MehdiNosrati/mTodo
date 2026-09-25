package io.mns.base.app.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.data.stats.StatisticsCalculator
import io.mns.base.app.data.stats.TaskStatistics
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InsightsViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val repository: TodoRepository by inject()
    private val prefs = application.getSharedPreferences("mtodo_settings", Context.MODE_PRIVATE)

    private val _statistics = MediatorLiveData<TaskStatistics>()
    val statistics: LiveData<TaskStatistics> get() = _statistics

    private var currentTodos: List<TodoItem> = emptyList()
    private var currentDoneItems: List<DoneItem> = emptyList()

    init {
        val todosLiveData = repository.loadTodoItems()
        val doneLiveData = repository.loadDoneItems()

        _statistics.addSource(todosLiveData) { todos ->
            currentTodos = todos ?: emptyList()
            recompute()
        }

        _statistics.addSource(doneLiveData) { doneItems ->
            currentDoneItems = doneItems ?: emptyList()
            recompute()
        }
    }

    private fun recompute() {
        val dailyGoal = prefs.getInt("pref_daily_goal", 3)
        _statistics.value = StatisticsCalculator.calculate(
            todos = currentTodos,
            doneItems = currentDoneItems,
            dailyGoal = dailyGoal
        )
    }
}
