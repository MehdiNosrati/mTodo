package io.mns.base.app.data.persistence

import androidx.room.*
import io.mns.base.app.data.DoneItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DoneItem)

    @Delete
    suspend fun delete(item: DoneItem)

    @Delete
    suspend fun deleteDoneItems(items: List<DoneItem>)

    @Query("SELECT * FROM doneItems WHERE id = :id LIMIT 1")
    suspend fun getDoneById(id: String): DoneItem?

    @Query("SELECT * FROM doneItems WHERE deletedAt IS NULL ORDER BY doneAt DESC")
    fun getDoneItems(): Flow<List<DoneItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoneItems(items: List<DoneItem>)

    @Query("SELECT * FROM doneItems WHERE deletedAt IS NULL ORDER BY doneAt DESC")
    suspend fun getAllDoneList(): List<DoneItem>

    @Query("SELECT * FROM doneItems WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getTrashedDoneItems(): Flow<List<DoneItem>>

    @Query("SELECT * FROM doneItems WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    suspend fun getTrashedDoneList(): List<DoneItem>

    @Query("UPDATE doneItems SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long)

    @Query("UPDATE doneItems SET deletedAt = NULL WHERE id = :id")
    suspend fun restoreFromTrash(id: String)

    @Query("DELETE FROM doneItems WHERE id = :id")
    suspend fun hardDelete(id: String)

    @Query("DELETE FROM doneItems WHERE deletedAt IS NOT NULL AND deletedAt < :olderThanMs")
    suspend fun purgeTrashOlderThan(olderThanMs: Long)

    @Query("DELETE FROM doneItems WHERE deletedAt IS NOT NULL")
    suspend fun emptyTrash()

    @Query("DELETE FROM doneItems")
    suspend fun clearAll()
}
