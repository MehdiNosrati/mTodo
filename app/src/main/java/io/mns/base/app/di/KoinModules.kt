package io.mns.base.app.di

import androidx.room.Room
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.data.persistence.TodoDataBase
import io.mns.base.app.ui.viewmodels.DoneViewModel
import io.mns.base.app.ui.viewmodels.HomeViewModel
import io.mns.base.app.ui.viewmodels.SettingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

object KoinModules {
    val appModule = module {

        single {
            Room.databaseBuilder(get(), TodoDataBase::class.java, "todo_db")
                .addMigrations(TodoDataBase.MIGRATION_2_3, TodoDataBase.MIGRATION_3_4, TodoDataBase.MIGRATION_4_5)
                .fallbackToDestructiveMigration()
                .build()
        }

        single {
            TodoRepository(get<TodoDataBase>().todoDao(), get<TodoDataBase>().doneDao()).apply {
                onDataChanged = {
                    io.mns.base.app.widget.TodoWidgetProvider.updateAllWidgets(get())
                }
            }
        }

        single { io.mns.base.app.notifications.ReminderManager(get()) }
        single { io.mns.base.app.data.backup.BackupManager(get(), get()) }

        viewModel { HomeViewModel(get()) }
        viewModel { DoneViewModel(get()) }
        viewModel { SettingViewModel(get()) }
        viewModel { io.mns.base.app.ui.viewmodels.InsightsViewModel(get()) }
        viewModel { io.mns.base.app.ui.viewmodels.TodoDetailViewModel(get()) }
    }
}
