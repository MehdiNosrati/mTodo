package io.mns.base.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import io.mns.base.app.R
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.notifications.ReminderManager
import io.mns.base.app.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodoWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val repository: TodoRepository by inject()
    private val reminderManager: ReminderManager by inject()

    companion object {
        const val EXTRA_START_ADD = "extra_start_add"
        const val EXTRA_TODO_ID = "extra_todo_id"
        const val EXTRA_IS_CHECK = "extra_is_check"
        const val ACTION_WIDGET_ITEM_CLICK = "io.mns.base.app.ACTION_WIDGET_ITEM_CLICK"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, TodoWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            if (appWidgetIds.isNotEmpty()) {
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view)
                val intent = Intent(context, TodoWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_WIDGET_ITEM_CLICK) {
            val todoId = intent.getStringExtra(EXTRA_TODO_ID)
            val isCheck = intent.getBooleanExtra(EXTRA_IS_CHECK, false)
            if (todoId != null) {
                if (isCheck) {
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val item = repository.getTodoById(todoId)
                            if (item != null) {
                                repository.done(item)
                                reminderManager.cancelReminder(todoId)
                            }
                        } finally {
                            pendingResult.finish()
                        }
                    }
                } else {
                    val openIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra(EXTRA_TODO_ID, todoId)
                    }
                    context.startActivity(openIntent)
                }
                return
            }
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_todo_layout)

            // Setup Add button click
            val addIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_START_ADD, true)
            }
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            val addPendingIntent = PendingIntent.getActivity(context, 101, addIntent, flags)
            views.setOnClickPendingIntent(R.id.widget_btn_add, addPendingIntent)

            // Setup ListView Adapter
            val serviceIntent = Intent(context, TodoWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            views.setRemoteAdapter(R.id.widget_list_view, serviceIntent)
            views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view)

            // Setup Item Click Template with Broadcast to handle both details and interactive check
            val clickIntent = Intent(context, TodoWidgetProvider::class.java).apply {
                action = ACTION_WIDGET_ITEM_CLICK
            }
            val clickPendingIntent = PendingIntent.getBroadcast(
                context,
                102,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_MUTABLE else 0)
            )
            views.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}
