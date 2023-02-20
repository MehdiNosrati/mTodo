package dev.mahdins.core.data

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.mahdins.core.data.persistence.TodoDataBase
import dev.mahdins.core.data.repository.LocalTodoRepository
import dev.mahdins.core.data.repository.TodoRepository

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, TodoDataBase::class.java, "todo_db").build()

    @Provides
    fun provideTodoRepository(dataBase: TodoDataBase): TodoRepository =
        LocalTodoRepository(dataBase.todoDao(), dataBase.doneDao())
}
