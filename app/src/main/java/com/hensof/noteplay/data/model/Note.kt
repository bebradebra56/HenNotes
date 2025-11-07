package com.hensof.noteplay.data.model

import com.hensof.noteplay.data.local.entity.NoteEntity
import java.util.Date

data class Note(
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

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        category = category,
        tags = tags,
        photoUris = photoUris,
        isFavorite = isFavorite,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun NoteEntity.toModel(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        category = category,
        tags = tags,
        photoUris = photoUris,
        isFavorite = isFavorite,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

