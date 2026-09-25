package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DoneViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val repository: TodoRepository by inject()

    private val _settingClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val settingClicked: LiveData<Boolean>
        get() = _settingClicked

    fun loadItems(): LiveData<List<DoneItem>> {
        return repository.loadDoneItems()
    }

    fun delete(item: DoneItem) {
        viewModelScope.launch {
            repository.deleteDoneItem(item)
        }
    }

    fun uncomplete(item: DoneItem) {
        viewModelScope.launch {
            repository.uncomplete(item)
        }
    }

    fun undoUncomplete(item: DoneItem) {
        viewModelScope.launch {
            repository.hardDeleteTodo(item.id)
            repository.restoreDoneItem(item)
        }
    }

    fun restoreDone(item: DoneItem) {
        viewModelScope.launch {
            repository.restoreDoneItem(item)
        }
    }

    fun settingClicked() {
        _settingClicked.postValue(true)
    }

    fun settingHandled() {
        _settingClicked.postValue(false)
    }
}
