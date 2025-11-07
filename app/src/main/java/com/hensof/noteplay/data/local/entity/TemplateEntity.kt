package com.hensof.noteplay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hensof.noteplay.data.model.NoteCategory

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val usageCount: Int = 0
)

