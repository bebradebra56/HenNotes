package com.hensof.noteplay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.hensof.noteplay.data.model.NoteCategory
import java.util.Date

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val tags: List<String> = emptyList(),
    val photoUris: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        return value?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun fromNoteCategory(value: NoteCategory): String {
        return value.name
    }

    @TypeConverter
    fun toNoteCategory(value: String): NoteCategory {
        return try {
            NoteCategory.valueOf(value)
        } catch (e: IllegalArgumentException) {
            NoteCategory.FREE_NOTES
        }
    }
}

