package dev.mahdins.feature.donelist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mahdins.core.data.model.DoneItem
import dev.mahdins.core.data.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DoneViewModel @Inject constructor(
    application: Application,
    private val repository: TodoRepository
) :
    AndroidViewModel(application) {

    private val _settingClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val settingClicked: LiveData<Boolean>
        get() = _settingClicked

    fun loadItems(): Flow<List<DoneItem>?> {
        return repository.loadDoneItems()
    }

    fun text() = "Some text from done view model"

    fun settingClicked() {
        _settingClicked.postValue(true)
    }

    fun settingHandled() {
        _settingClicked.postValue(false)
    }
}
