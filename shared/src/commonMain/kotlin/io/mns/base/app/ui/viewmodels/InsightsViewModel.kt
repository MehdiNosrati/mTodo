package io.mns.base.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.data.settings.AppSettings
import io.mns.base.app.data.stats.StatisticsCalculator
import io.mns.base.app.data.stats.TaskStatistics
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InsightsViewModel(
    repository: TodoRepository? = null,
    settings: AppSettings? = null,
    @Suppress("UNUSED_PARAMETER") application: Any? = null
) : ViewModel(), KoinComponent {

    private val injectedRepo: TodoRepository by inject()
    private val injectedSettings: AppSettings by inject()

    private val repo: TodoRepository = repository ?: injectedRepo
    private val appSettings: AppSettings = settings ?: injectedSettings

    val statistics: StateFlow<TaskStatistics> = combine(
        repo.loadTodoItems(),
        repo.loadDoneItems()
    ) { todos, doneItems ->
        val dailyGoal = appSettings.getDailyGoal()
        StatisticsCalculator.calculate(
            todos = todos,
            doneItems = doneItems,
            dailyGoal = dailyGoal
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, TaskStatistics())
}
