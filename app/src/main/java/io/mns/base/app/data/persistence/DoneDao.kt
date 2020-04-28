package io.mns.base.app.data.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoItem

@Dao
interface DoneDao {
    @Insert
    suspend fun insert(item: DoneItem)

    @Delete
    suspend fun delete(item: DoneItem)

    @Query("select * from doneItems order by doneAt desc")
    fun getDoneItems(): LiveData<List<DoneItem>>
}