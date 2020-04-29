package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class DoneViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
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