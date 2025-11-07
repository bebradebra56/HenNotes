package com.hensof.noteplay.data.repository

import com.hensof.noteplay.data.local.HenNotesDatabase
import com.hensof.noteplay.data.local.dao.TemplateDao
import com.hensof.noteplay.data.model.NoteCategory
import com.hensof.noteplay.data.model.Template
import com.hensof.noteplay.data.model.toEntity
import com.hensof.noteplay.data.model.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TemplateRepository(private val templateDao: TemplateDao) {

    fun getAllTemplates(): Flow<List<Template>> {
        return templateDao.getAllTemplates().map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getTemplatesByCategory(category: NoteCategory): Flow<List<Template>> {
        return templateDao.getTemplatesByCategory(category).map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getTemplateById(id: Long): Flow<Template?> {
        return templateDao.getTemplateById(id).map { it?.toModel() }
    }

    suspend fun insertTemplate(template: Template): Long {
        return templateDao.insertTemplate(template.toEntity())
    }

    suspend fun updateTemplate(template: Template) {
        templateDao.updateTemplate(template.toEntity())
    }

    suspend fun deleteTemplate(template: Template) {
        templateDao.deleteTemplate(template.toEntity())
    }

    suspend fun incrementUsageCount(id: Long) {
        templateDao.incrementUsageCount(id)
    }

    suspend fun initializeDefaultTemplates() {
        val existingTemplates = templateDao.getAllTemplates().first()
        if (existingTemplates.isEmpty()) {
            HenNotesDatabase.getDefaultTemplates().forEach { template ->
                templateDao.insertTemplate(template)
            }
        }
    }
}

