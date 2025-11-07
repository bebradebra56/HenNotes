package com.hensof.noteplay.data.repository

import com.hensof.noteplay.data.local.dao.NoteDao
import com.hensof.noteplay.data.model.Note
import com.hensof.noteplay.data.model.NoteCategory
import com.hensof.noteplay.data.model.toEntity
import com.hensof.noteplay.data.model.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getNoteById(id: Long): Flow<Note?> {
        return noteDao.getNoteById(id).map { it?.toModel() }
    }

    fun getNotesByCategory(category: NoteCategory): Flow<List<Note>> {
        return noteDao.getNotesByCategory(category).map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getFavoriteNotes(): Flow<List<Note>> {
        return noteDao.getFavoriteNotes().map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query).map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getRecentNotes(limit: Int = 5): Flow<List<Note>> {
        return noteDao.getRecentNotes(limit).map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getNotesCountByCategory(category: NoteCategory): Flow<Int> {
        return noteDao.getNotesCountByCategory(category)
    }

    fun getTotalNotesCount(): Flow<Int> {
        return noteDao.getTotalNotesCount()
    }

    suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(note.toEntity())
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toEntity())
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toEntity())
    }

    suspend fun deleteNoteById(id: Long) {
        noteDao.deleteNoteById(id)
    }

    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }
}

