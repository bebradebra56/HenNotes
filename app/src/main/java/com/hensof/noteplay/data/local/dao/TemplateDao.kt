package com.hensof.noteplay.data.local.dao

import androidx.room.*
import com.hensof.noteplay.data.local.entity.TemplateEntity
import com.hensof.noteplay.data.model.NoteCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Query("SELECT * FROM templates ORDER BY usageCount DESC")
    fun getAllTemplates(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE category = :category ORDER BY usageCount DESC")
    fun getTemplatesByCategory(category: NoteCategory): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE id = :id")
    fun getTemplateById(id: Long): Flow<TemplateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: TemplateEntity): Long

    @Update
    suspend fun updateTemplate(template: TemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: TemplateEntity)

    @Query("UPDATE templates SET usageCount = usageCount + 1 WHERE id = :id")
    suspend fun incrementUsageCount(id: Long)
}

