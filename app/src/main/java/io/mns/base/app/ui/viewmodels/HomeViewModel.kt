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
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val repository: TodoRepository by inject()

    private val _addClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val addClicked: LiveData<Boolean>
        get() = _addClicked


    fun insertItem(title: String) {
        viewModelScope.launch {
            repository.insertTodoItem(TodoItem(UUID.randomUUID().toString(), Date().time, title))
        }
    }

    fun loadItems(): LiveData<List<TodoItem>> {
        return repository.loadTodoItems()
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
}