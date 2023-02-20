package dev.mahdins.feature.todolist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mahdins.core.data.model.TodoItem
import dev.mahdins.core.data.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import java.util.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    private val _addClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val addClicked: LiveData<Boolean>
        get() = _addClicked

    private val _settingClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val settingClicked: LiveData<Boolean>
        get() = _settingClicked

    fun insertItem(title: String) {
        viewModelScope.launch {
            repository.insertTodoItem(
                TodoItem(
                    UUID.randomUUID().toString(), Date().time, title
                )
            )
        }
    }

    fun loadItems(): Flow<List<TodoItem>?> {
        return repository.loadTodoItems()
    }

    fun text() = "some text form VM"

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
}
