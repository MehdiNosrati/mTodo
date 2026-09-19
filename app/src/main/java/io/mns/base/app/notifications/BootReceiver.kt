package io.mns.base.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.mns.base.app.data.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val repository: TodoRepository by inject()
    private val reminderManager: ReminderManager by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            CoroutineScope(Dispatchers.IO).launch {
                val todos = repository.getAllTodosList()
                val now = System.currentTimeMillis()
                for (todo in todos) {
                    val due = todo.dueDate
                    if (due != null && due > now) {
                        reminderManager.scheduleReminder(todo)
                    }
                }
            }
        }
    }
}
