package com.hensof.noteplay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hensof.noteplay.data.model.Note
import com.hensof.noteplay.data.model.NoteCategory
import com.hensof.noteplay.data.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

data class CategoryStats(
    val category: NoteCategory,
    val count: Int
)

data class StatisticsUiState(
    val totalNotes: Int = 0,
    val categoryStats: List<CategoryStats> = emptyList(),
    val favoriteCount: Int = 0,
    val isLoading: Boolean = true
)

class StatisticsViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState(isLoading = true))
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val totalNotes = repository.getTotalNotesCount().first()
                val favoriteNotes = repository.getFavoriteNotes().first()
                val categoryStats = NoteCategory.values().map { category ->
                    CategoryStats(category, repository.getNotesCountByCategory(category).first())
                }
                
                _uiState.value = StatisticsUiState(
                    totalNotes = totalNotes,
                    categoryStats = categoryStats,
                    favoriteCount = favoriteNotes.size,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = StatisticsUiState(
                    totalNotes = 0,
                    categoryStats = emptyList(),
                    favoriteCount = 0,
                    isLoading = false
                )
            }
        }
    }

    fun toggleFavorite(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isFavorite = !note.isFavorite, updatedAt = java.util.Date()))
            loadStatistics() // Перезагружаем статистику
        }
    }
}

