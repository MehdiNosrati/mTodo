package dev.mahdins.mtodo.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.mahdins.mtodo.data.DoneItem
import dev.mahdins.mtodo.data.TodoRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DoneViewModel(application: Application) : AndroidViewModel(application),
    KoinComponent {
    private val repository: TodoRepository by inject()

    private val _settingClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val settingClicked: LiveData<Boolean>
        get() = _settingClicked

    fun loadItems(): LiveData<List<DoneItem>> {
        return repository.loadDoneItems()
    }

    fun settingClicked() {
        _settingClicked.postValue(true)
    }

    fun settingHandled() {
        _settingClicked.postValue(false)
    }
}
