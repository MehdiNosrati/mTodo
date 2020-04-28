package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class HomeViewModel(application: Application): AndroidViewModel(application), KoinComponent {
    private val repository: TodoRepository by inject()

    fun insert() {
        viewModelScope.launch {
            repository.insert(TodoItem("oonee", false))
        }
    }

    fun load(): LiveData<List<TodoItem>?> {
        return repository.load()
    }

    fun update(item: TodoItem) {
        viewModelScope.launch {
            repository.update(item)
        }
    }
}