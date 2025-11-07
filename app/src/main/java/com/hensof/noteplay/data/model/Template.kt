package com.hensof.noteplay.data.model

import com.hensof.noteplay.data.local.entity.TemplateEntity

data class Template(
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val usageCount: Int = 0
)

fun Template.toEntity(): TemplateEntity {
    return TemplateEntity(
        id = id,
        title = title,
        content = content,
        category = category,
        usageCount = usageCount
    )
}

fun TemplateEntity.toModel(): Template {
    return Template(
        id = id,
        title = title,
        content = content,
        category = category,
        usageCount = usageCount
    )
}

