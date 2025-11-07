package com.hensof.noteplay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hensof.noteplay.data.model.Note
import com.hensof.noteplay.data.model.Template
import com.hensof.noteplay.data.repository.NoteRepository
import com.hensof.noteplay.data.repository.TemplateRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class HomeUiState(
    val recentNotes: List<Note> = emptyList(),
    val totalNotesCount: Int = 0,
    val templates: List<Template> = emptyList(),
    val tipOfTheDay: String = "",
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val noteRepository: NoteRepository,
    private val templateRepository: TemplateRepository
) : ViewModel() {

    private val tips = listOf(
        "Collect eggs twice daily for best freshness!",
        "Keep a regular feeding schedule for healthy chickens.",
        "Check water supply regularly, especially in hot weather.",
        "Clean the coop weekly to prevent diseases.",
        "Monitor egg production patterns for health insights.",
        "Store eggs in a cool, dry place.",
        "Track expenses to maximize farm profitability.",
        "Regular health checks prevent major issues.",
        "Keep vaccination records up to date.",
        "Fresh feed produces better quality eggs!"
    )

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        initializeDefaultTemplates()
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val recentNotes = noteRepository.getRecentNotes(5).first()
                val totalCount = noteRepository.getTotalNotesCount().first()
                val templates = templateRepository.getAllTemplates().first()
                
                _uiState.value = HomeUiState(
                    recentNotes = recentNotes,
                    totalNotesCount = totalCount,
                    templates = templates.take(3),
                    tipOfTheDay = tips.random(),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    recentNotes = emptyList(),
                    totalNotesCount = 0,
                    templates = emptyList(),
                    tipOfTheDay = tips.random(),
                    isLoading = false
                )
            }
        }
    }

    private fun initializeDefaultTemplates() {
        viewModelScope.launch {
            templateRepository.initializeDefaultTemplates()
        }
    }

    fun toggleFavorite(note: Note) {
        viewModelScope.launch {
            noteRepository.updateNote(note.copy(isFavorite = !note.isFavorite, updatedAt = java.util.Date()))
            loadData() // Перезагружаем данные
        }
    }
}

