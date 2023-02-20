package dev.mahdins.core.data.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.mahdins.core.data.model.DoneItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DoneItem)

    @Query("select * from doneItems order by doneAt desc")
    fun getDoneItems(): Flow<List<DoneItem>>
}
