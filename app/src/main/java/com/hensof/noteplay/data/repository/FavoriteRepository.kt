package com.hensof.noteplay.data.repository

import com.hensof.noteplay.data.model.Note
import com.hensof.noteplay.data.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class FavoriteRepository(private val noteRepository: NoteRepository) {
    
    fun toggleFavorite(note: Note): Flow<Unit> {
        return kotlinx.coroutines.flow.flow {
            val updatedNote = note.copy(
                isFavorite = !note.isFavorite,
                updatedAt = Date()
            )
            noteRepository.updateNote(updatedNote)
            emit(Unit)
        }
    }
}
