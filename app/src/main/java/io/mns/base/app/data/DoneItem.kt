package io.mns.base.app.data

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doneItems")
data class DoneItem(
    @PrimaryKey val id: String,
    val doneAt: Long,
    @Bindable val title: String
) : BaseObservable()