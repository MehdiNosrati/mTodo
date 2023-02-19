package dev.mahdins.mtodo.di

import androidx.room.Room
import dev.mahdins.mtodo.data.TodoRepository
import dev.mahdins.mtodo.data.persistence.TodoDataBase
import org.koin.dsl.module

object KoinModules {
    val appModule = module {

        single {
            Room.databaseBuilder(get(), TodoDataBase::class.java, "todo_db").build()
        }

        single {
            TodoRepository(get<TodoDataBase>().todoDao(), get<TodoDataBase>().doneDao())
        }
    }
}
