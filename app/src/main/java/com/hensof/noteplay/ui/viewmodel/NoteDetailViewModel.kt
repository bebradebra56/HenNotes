package com.hensof.noteplay.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hensof.noteplay.data.model.Note
import com.hensof.noteplay.data.model.NoteCategory
import com.hensof.noteplay.data.repository.NoteRepository
import com.hensof.noteplay.utils.ImageUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

data class NoteDetailUiState(
    val note: Note? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class NoteDetailViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _category = MutableStateFlow(NoteCategory.FREE_NOTES)
    val category: StateFlow<NoteCategory> = _category.asStateFlow()

    private val _tags = MutableStateFlow<List<String>>(emptyList())
    val tags: StateFlow<List<String>> = _tags.asStateFlow()

    private val _photoUris = MutableStateFlow<List<String>>(emptyList())
    val photoUris: StateFlow<List<String>> = _photoUris.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var currentNoteId: Long = 0

    fun loadNote(noteId: Long) {
        if (currentNoteId == noteId) return // Уже загружена
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getNoteById(noteId).firstOrNull()?.let { note ->
                currentNoteId = note.id
                _title.value = note.title
                _content.value = note.content
                _category.value = note.category
                _tags.value = note.tags
                _photoUris.value = note.photoUris
                _isFavorite.value = note.isFavorite
                _uiState.value = _uiState.value.copy(note = note, isLoading = false)
            } ?: run {
                // Если заметка не найдена, сбрасываем состояние
                resetState()
            }
        }
    }
    
    fun migratePhotoUris(context: Context) {
        viewModelScope.launch {
            val currentPhotoUris = _photoUris.value
            val migratedUris = mutableListOf<String>()
            
            for (uri in currentPhotoUris) {
                if (!uri.startsWith("/")) {
                    // This is a temporary URI, try to save it
                    try {
                        val savedPath = ImageUtils.saveImageToInternalStorage(context, Uri.parse(uri))
                        if (savedPath != null) {
                            migratedUris.add(savedPath)
                        } else {
                            migratedUris.add(uri) // Keep original if migration fails
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        migratedUris.add(uri) // Keep original if migration fails
                    }
                } else {
                    migratedUris.add(uri) // Already a file path
                }
            }
            
            if (migratedUris != currentPhotoUris) {
                _photoUris.value = migratedUris
            }
        }
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setContent(content: String) {
        _content.value = content
    }

    fun setCategory(category: NoteCategory) {
        _category.value = category
    }

    fun addTag(tag: String) {
        if (tag.isNotBlank() && !_tags.value.contains(tag)) {
            _tags.value = _tags.value + tag
        }
    }

    fun removeTag(tag: String) {
        _tags.value = _tags.value - tag
    }

    fun addPhotoUri(uri: String) {
        _photoUris.value = _photoUris.value + uri
    }
    
    fun addPhotoUri(context: Context, uri: String) {
        viewModelScope.launch {
            try {
                val savedPath = ImageUtils.saveImageToInternalStorage(context, android.net.Uri.parse(uri))
                if (savedPath != null) {
                    _photoUris.value = _photoUris.value + savedPath
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback to original URI if saving fails
                _photoUris.value = _photoUris.value + uri
            }
        }
    }

    fun removePhotoUri(uri: String) {
        _photoUris.value = _photoUris.value - uri
    }
    
    fun removePhotoUri(context: Context, uri: String) {
        // Check if it's a file path (starts with /) or URI
        if (uri.startsWith("/")) {
            ImageUtils.deleteImage(context, uri)
        }
        _photoUris.value = _photoUris.value - uri
    }

    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
        // Обновляем заметку в базе данных
        viewModelScope.launch {
            if (currentNoteId > 0) {
                val note = Note(
                    id = currentNoteId,
                    title = _title.value,
                    content = _content.value,
                    category = _category.value,
                    tags = _tags.value,
                    photoUris = _photoUris.value,
                    isFavorite = _isFavorite.value,
                    createdAt = _uiState.value.note?.createdAt ?: Date(),
                    updatedAt = Date()
                )
                repository.updateNote(note)
            }
        }
    }

    fun saveNote(onSaved: () -> Unit) {
        viewModelScope.launch {
            try {
                val note = Note(
                    id = currentNoteId,
                    title = _title.value,
                    content = _content.value,
                    category = _category.value,
                    tags = _tags.value,
                    photoUris = _photoUris.value,
                    isFavorite = _isFavorite.value,
                    createdAt = _uiState.value.note?.createdAt ?: Date(),
                    updatedAt = Date()
                )
                
                if (currentNoteId == 0L) {
                    repository.insertNote(note)
                } else {
                    repository.updateNote(note)
                }
                
                _uiState.value = _uiState.value.copy(isSaved = true)
                onSaved()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun loadTemplate(templateTitle: String, templateContent: String, templateCategory: NoteCategory) {
        _title.value = templateTitle
        _content.value = templateContent
        _category.value = templateCategory
    }

    fun resetState() {
        currentNoteId = 0
        _title.value = ""
        _content.value = ""
        _category.value = NoteCategory.FREE_NOTES
        _tags.value = emptyList()
        _photoUris.value = emptyList()
        _isFavorite.value = false
        _uiState.value = NoteDetailUiState()
    }
}

