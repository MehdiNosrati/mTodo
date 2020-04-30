package io.mns.base.app.di

import androidx.room.Room
import io.mns.androidlib.NotificationUtil
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.data.persistence.TodoDataBase
import org.koin.dsl.module

object KoinModules {
    val appModule = module {
        single {
            NotificationUtil(get())
        }

        single {
            Room.databaseBuilder(get(), TodoDataBase::class.java, "todo_db").build()
        }

        single {
            TodoRepository(get<TodoDataBase>().todoDao(), get<TodoDataBase>().doneDao())
        }
    }
}
