package io.mns.base.app.di

import io.mns.base.app.data.TodoRepository
import io.mns.base.app.data.backup.BackupManager
import io.mns.base.app.data.persistence.TodoDataBase
import io.mns.base.app.data.persistence.getDatabaseBuilder
import io.mns.base.app.data.persistence.getRoomDatabase
import io.mns.base.app.data.settings.AndroidAppSettings
import io.mns.base.app.data.settings.AppSettings
import io.mns.base.app.notifications.ReminderManager
import io.mns.base.app.ui.viewmodels.*
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

object KoinModules {
    val appModule = module {

        single<TodoDataBase> {
            getRoomDatabase(getDatabaseBuilder(get()))
        }

        single<ReminderManager> { io.mns.base.app.notifications.AndroidReminderManager(get()) }
        single<AppSettings> { AndroidAppSettings(get()) }

        single {
            TodoRepository(get<TodoDataBase>().todoDao(), get<TodoDataBase>().doneDao()).apply {
                onDataChanged = {
                    io.mns.base.app.widget.TodoWidgetProvider.updateAllWidgets(get())
                }
            }
        }

        single { BackupManager(get()) }

        viewModel { HomeViewModel(get(), get()) }
        viewModel { DoneViewModel(get()) }
        viewModel { SettingViewModel(get(), get(), get(), get()) }
        viewModel { InsightsViewModel(get(), get()) }
        viewModel { TodoDetailViewModel(get(), get()) }
    }
}
