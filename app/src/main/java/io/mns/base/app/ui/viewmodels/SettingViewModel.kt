package io.mns.base.app.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.mns.base.app.data.SortOrder
import io.mns.base.app.data.backup.BackupManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val backupManager: BackupManager by inject()
    private val prefs = application.getSharedPreferences("mtodo_settings", Context.MODE_PRIVATE)

    private val _sortOrder = MutableStateFlow(
        try {
            SortOrder.valueOf(prefs.getString("pref_sort_order", SortOrder.CREATION_DATE_DESC.name) ?: SortOrder.CREATION_DATE_DESC.name)
        } catch (e: Exception) {
            SortOrder.CREATION_DATE_DESC
        }
    )
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _backClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val backClicked: LiveData<Boolean>
        get() = _backClicked

    private val _toggleTheme: MutableLiveData<Boolean> = MutableLiveData(false)
    val toggleTheme: LiveData<Boolean>
        get() = _toggleTheme

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        prefs.edit().putString("pref_sort_order", order.name).apply()
    }

    fun exportBackup(uri: Uri, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val outputStream = getApplication<Application>().contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    backupManager.exportToStream(outputStream)
                    onResult(true, "Backup exported successfully!")
                } else {
                    onResult(false, "Could not open file for writing.")
                }
            } catch (e: Exception) {
                onResult(false, "Export failed: ${e.message}")
            }
        }
    }

    fun restoreBackup(uri: Uri, overwrite: Boolean = false, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val (todosCount, doneCount) = backupManager.restoreFromStream(inputStream, overwrite)
                    onResult(true, "Restored $todosCount active tasks and $doneCount completed tasks!")
                } else {
                    onResult(false, "Could not read backup file.")
                }
            } catch (e: Exception) {
                onResult(false, "Restore failed: Invalid backup format.")
            }
        }
    }

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
