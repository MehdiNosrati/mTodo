package io.mns.base.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DoneViewModel(
    repository: TodoRepository? = null,
    @Suppress("UNUSED_PARAMETER") application: Any? = null
) : ViewModel(), KoinComponent {

    private val injectedRepo: TodoRepository by inject()
    private val repo: TodoRepository = repository ?: injectedRepo

    private val _settingClicked = MutableStateFlow(false)
    val settingClicked: StateFlow<Boolean> = _settingClicked.asStateFlow()

    val doneItems: StateFlow<List<DoneItem>> = repo.loadDoneItems()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun loadItems(): Flow<List<DoneItem>> = repo.loadDoneItems()

    fun delete(item: DoneItem) {
        viewModelScope.launch {
            repo.deleteDoneItem(item)
        }
    }

    fun uncomplete(item: DoneItem) {
        viewModelScope.launch {
            repo.uncomplete(item)
        }
    }

    fun undoUncomplete(item: DoneItem) {
        viewModelScope.launch {
            repo.hardDeleteTodo(item.id)
            repo.restoreDoneItem(item)
        }
    }

    fun restoreDone(item: DoneItem) {
        viewModelScope.launch {
            repo.restoreDoneItem(item)
        }
    }

    fun settingClicked() {
        _settingClicked.value = true
    }

    fun settingHandled() {
        _settingClicked.value = false
    }
}
