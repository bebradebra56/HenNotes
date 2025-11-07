package com.hensof.noteplay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hensof.noteplay.data.model.Note
import com.hensof.noteplay.data.model.NoteCategory
import com.hensof.noteplay.data.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<NoteCategory?>(null)
    val selectedCategory: StateFlow<NoteCategory?> = _selectedCategory.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    private val _uiState = MutableStateFlow(NotesUiState(isLoading = true))
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val notes = when {
                    _showFavoritesOnly.value -> repository.getFavoriteNotes().first()
                    _searchQuery.value.isNotBlank() -> repository.searchNotes(_searchQuery.value).first()
                    _selectedCategory.value != null -> repository.getNotesByCategory(_selectedCategory.value!!).first()
                    else -> repository.getAllNotes().first()
                }
                
                _uiState.value = NotesUiState(
                    notes = notes,
                    isLoading = false,
                    isEmpty = notes.isEmpty()
                )
            } catch (e: Exception) {
                _uiState.value = NotesUiState(
                    notes = emptyList(),
                    isLoading = false,
                    isEmpty = true
                )
            }
        }
    }

    fun setSearchQuery(query: String) {
        if (_searchQuery.value != query) {
            _searchQuery.value = query
            loadNotes()
        }
    }

    fun setSelectedCategory(category: NoteCategory?) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            loadNotes()
        }
    }

    fun toggleFavoritesOnly() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
        loadNotes()
    }

    fun toggleFavorite(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isFavorite = !note.isFavorite, updatedAt = Date()))
            loadNotes() // Перезагружаем список
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            try {
                // Удаляем связанные изображения
                note.photoUris.forEach { uri ->
                    if (uri.startsWith("/")) {
                        try {
                            val file = java.io.File(uri)
                            if (file.exists()) {
                                file.delete()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                repository.deleteNote(note)
                loadNotes() // Перезагружаем список
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

