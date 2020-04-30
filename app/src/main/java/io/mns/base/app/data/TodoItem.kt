package io.mns.base.app.data

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoItem(
    @PrimaryKey val id: String,
    val createdAt: Long,
    @Bindable val title: String
) : BaseObservable()
