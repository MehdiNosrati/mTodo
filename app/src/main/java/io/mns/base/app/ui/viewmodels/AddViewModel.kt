package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AddViewModel(application: Application) : AndroidViewModel(application) {
    private val _addClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val addClicked: LiveData<Boolean>
        get() = _addClicked

    private val _backClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val backClicked: LiveData<Boolean>
        get() = _backClicked

    fun addClicked() {
        _addClicked.postValue(true)
    }

    fun addHandled() {
        _addClicked.postValue(false)
    }

    fun backClicked() {
        _backClicked.postValue(true)
    }

    fun backHandled() {
        _backClicked.postValue(false)
    }
}
