package com.hensof.noteplay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hensof.noteplay.data.model.NoteCategory
import com.hensof.noteplay.data.model.Template
import com.hensof.noteplay.data.repository.TemplateRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TemplatesUiState(
    val templates: List<Template> = emptyList(),
    val isLoading: Boolean = true
)

class TemplatesViewModel(private val repository: TemplateRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<NoteCategory?>(null)
    private val _uiState = MutableStateFlow(TemplatesUiState(isLoading = true))
    val uiState: StateFlow<TemplatesUiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val templates = if (_selectedCategory.value != null) {
                    repository.getTemplatesByCategory(_selectedCategory.value!!).first()
                } else {
                    repository.getAllTemplates().first()
                }
                
                _uiState.value = TemplatesUiState(
                    templates = templates,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = TemplatesUiState(
                    templates = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    fun setSelectedCategory(category: NoteCategory?) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            loadTemplates()
        }
    }

    fun incrementTemplateUsage(templateId: Long) {
        viewModelScope.launch {
            repository.incrementUsageCount(templateId)
        }
    }
}

