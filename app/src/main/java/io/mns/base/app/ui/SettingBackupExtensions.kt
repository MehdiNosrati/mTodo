package io.mns.base.app.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import io.mns.base.app.ui.viewmodels.SettingViewModel
import kotlinx.coroutines.launch

fun SettingViewModel.exportBackup(context: Context, uri: Uri, onResult: (Boolean, String) -> Unit) {
    viewModelScope.launch {
        try {
            val outputStream = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                val json = backup.createBackupJson()
                outputStream.bufferedWriter().use { it.write(json) }
                onResult(true, "Backup exported successfully!")
            } else {
                onResult(false, "Could not open file for writing.")
            }
        } catch (e: Exception) {
            onResult(false, "Export failed: ${e.message}")
        }
    }
}

fun SettingViewModel.restoreBackup(context: Context, uri: Uri, overwrite: Boolean = false, onResult: (Boolean, String) -> Unit) {
    viewModelScope.launch {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val jsonStr = inputStream.bufferedReader().use { it.readText() }
                val (todosCount, doneCount) = backup.restoreFromJson(jsonStr, overwrite)
                onResult(true, "Restored $todosCount active tasks and $doneCount completed tasks!")
            } else {
                onResult(false, "Could not read backup file.")
            }
        } catch (e: Exception) {
            onResult(false, "Restore failed: Invalid backup format.")
        }
    }
}
