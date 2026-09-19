package io.mns.base.app.data.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.mns.base.app.data.DoneItem

@Dao
interface DoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DoneItem)

    @androidx.room.Delete
    suspend fun delete(item: DoneItem)

    @Query("SELECT * FROM doneItems WHERE id = :id LIMIT 1")
    suspend fun getDoneById(id: String): DoneItem?

    @Query("select * from doneItems order by doneAt desc")
    fun getDoneItems(): LiveData<List<DoneItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoneItems(items: List<DoneItem>)

    @Query("SELECT * FROM doneItems ORDER BY doneAt DESC")
    suspend fun getAllDoneList(): List<DoneItem>

    @Query("DELETE FROM doneItems")
    suspend fun clearAll()
}
