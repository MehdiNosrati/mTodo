package io.mns.base.app.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import io.mns.base.app.R
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReminderReceiver : BroadcastReceiver(), KoinComponent {

    private val repository: TodoRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val todoId = intent.getStringExtra(ReminderManager.EXTRA_TODO_ID) ?: return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        when (action) {
            ReminderManager.ACTION_MARK_DONE -> {
                notificationManager.cancel(todoId.hashCode())
                CoroutineScope(Dispatchers.IO).launch {
                    val item = repository.getTodoById(todoId)
                    if (item != null) {
                        repository.done(item)
                    }
                }
            }
            ReminderManager.ACTION_REMINDER -> {
                val title = intent.getStringExtra(ReminderManager.EXTRA_TITLE) ?: "Task Reminder"
                val description = intent.getStringExtra(ReminderManager.EXTRA_DESCRIPTION).orEmpty()

                val openAppIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val openPendingIntent = PendingIntent.getActivity(
                    context,
                    todoId.hashCode(),
                    openAppIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
                )

                val doneIntent = Intent(context, ReminderReceiver::class.java).apply {
                    this.action = ReminderManager.ACTION_MARK_DONE
                    putExtra(ReminderManager.EXTRA_TODO_ID, todoId)
                }
                val donePendingIntent = PendingIntent.getBroadcast(
                    context,
                    todoId.hashCode() + 1,
                    doneIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
                )

                val notification = NotificationCompat.Builder(context, ReminderManager.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(if (description.isNotBlank()) description else "This task is due now!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(openPendingIntent)
                    .addAction(R.drawable.ic_launcher_foreground, "Mark Done", donePendingIntent)
                    .build()

                notificationManager.notify(todoId.hashCode(), notification)
            }
        }
    }
}
