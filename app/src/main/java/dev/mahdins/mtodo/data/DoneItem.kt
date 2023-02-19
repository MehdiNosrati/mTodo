package dev.mahdins.mtodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

val df = SimpleDateFormat("MM/dd", Locale.US)

@Entity(tableName = "doneItems")
data class DoneItem(
    @PrimaryKey val id: String,
    val doneAt: Long,
    val title: String
) {
    val date: String
        get() = df.format(Date(doneAt))
}
