package io.mns.base.app.di

import androidx.room.Room
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.data.persistence.TodoDataBase
import io.mns.base.app.ui.viewmodels.DoneViewModel
import io.mns.base.app.ui.viewmodels.HomeViewModel
import io.mns.base.app.ui.viewmodels.SettingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object KoinModules {
    val appModule = module {

        single {
            Room.databaseBuilder(get(), TodoDataBase::class.java, "todo_db").build()
        }

        single {
            TodoRepository(get<TodoDataBase>().todoDao(), get<TodoDataBase>().doneDao())
        }

        viewModel { HomeViewModel(get()) }
        viewModel { DoneViewModel(get()) }
        viewModel { SettingViewModel(get()) }
    }
}
