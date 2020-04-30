package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingViewModel(application: Application) : AndroidViewModel(application) {

    private val _backClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val backClicked: LiveData<Boolean>
        get() = _backClicked

    private val _toggleTheme: MutableLiveData<Boolean> = MutableLiveData(false)
    val toggleTheme: LiveData<Boolean>
        get() = _toggleTheme

    fun backClicked() {
        _backClicked.postValue(true)
    }

    fun backHandled() {
        _backClicked.postValue(false)
    }

    fun toggleTheme() {
        _toggleTheme.postValue(true)
    }

    fun themeToggled() {
        _toggleTheme.postValue(false)
    }
}
