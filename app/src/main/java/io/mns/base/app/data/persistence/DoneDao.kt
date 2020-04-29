package io.mns.base.app.data.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.mns.base.app.data.DoneItem

@Dao
interface DoneDao {
    @Insert
    suspend fun insert(item: DoneItem)

    @Query("select * from doneItems order by doneAt desc")
    fun getDoneItems(): LiveData<List<DoneItem>>
}