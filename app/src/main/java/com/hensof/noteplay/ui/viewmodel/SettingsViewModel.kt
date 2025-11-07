package com.hensof.noteplay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hensof.noteplay.data.repository.NoteRepository
import com.hensof.noteplay.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean> = preferencesRepository.isDarkTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    fun toggleTheme() {
        viewModelScope.launch {
            preferencesRepository.setDarkTheme(!isDarkTheme.value)
        }
    }

    fun deleteAllNotes() {
        viewModelScope.launch {
            try {
                // Получаем все заметки для удаления связанных изображений
                val allNotes = noteRepository.getAllNotes().stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Eagerly,
                    initialValue = emptyList()
                ).value
                
                // Удаляем все связанные изображения
                allNotes.forEach { note ->
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
                }
                
                // Удаляем все заметки из базы данных
                noteRepository.deleteAllNotes()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

