package io.mns.base.app.data

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoItem(@Bindable val title: String, @Bindable var done: Boolean) : BaseObservable() {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}