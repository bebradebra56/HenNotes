package com.hensof.noteplay.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hensof.noteplay.data.local.HenNotesDatabase
import com.hensof.noteplay.data.model.Note
import com.hensof.noteplay.data.repository.NoteRepository
import com.hensof.noteplay.data.repository.PreferencesRepository
import com.hensof.noteplay.data.repository.TemplateRepository
import com.hensof.noteplay.ui.screens.*
import com.hensof.noteplay.ui.viewmodel.*

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    val context = LocalContext.current
    val database = remember { HenNotesDatabase.getDatabase(context) }
    val noteRepository = remember { NoteRepository(database.noteDao()) }
    val templateRepository = remember { TemplateRepository(database.templateDao()) }
    val preferencesRepository = remember { PreferencesRepository(context) }

    // Создаем ViewModels один раз
    val homeViewModel = remember { HomeViewModel(noteRepository, templateRepository) }
    val notesViewModel = remember { NotesViewModel(noteRepository) }
    val statisticsViewModel = remember { StatisticsViewModel(noteRepository) }
    val settingsViewModel = remember { SettingsViewModel(preferencesRepository, noteRepository) }
    
    // Функция для обновления избранного во всех экранах
    fun updateFavorites() {
        homeViewModel.loadData()
        notesViewModel.loadNotes()
        statisticsViewModel.loadStatistics()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            DisposableEffect(Unit) {
                homeViewModel.loadData()
                onDispose { }
            }
            HomeScreen(
                viewModel = homeViewModel,
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                },
                onCreateNote = {
                    navController.navigate(Screen.NoteDetail.createRoute(0))
                },
                onTemplateClick = { template ->
                    navController.navigate(Screen.NoteDetail.createRoute(0))
                },
                onFavoriteToggle = { note ->
                    homeViewModel.toggleFavorite(note)
                    updateFavorites()
                }
            )
        }

        composable(Screen.Notes.route) {
            DisposableEffect(Unit) {
                notesViewModel.loadNotes()
                onDispose { }
            }
            NotesScreen(
                viewModel = notesViewModel,
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                },
                onCreateNote = {
                    navController.navigate(Screen.NoteDetail.createRoute(0))
                },
                onFavoriteToggle = { note ->
                    notesViewModel.toggleFavorite(note)
                    updateFavorites()
                },
                onDeleteNote = { note ->
                    notesViewModel.deleteNote(note)
                    updateFavorites()
                }
            )
        }

        composable(Screen.Categories.route) {
            CategoriesScreen(
                onCategoryClick = { category ->
                    navController.navigate(Screen.Notes.route)
                }
            )
        }

        composable(Screen.Statistics.route) {
            DisposableEffect(Unit) {
                statisticsViewModel.loadStatistics()
                onDispose { }
            }
            StatisticsScreen(viewModel = statisticsViewModel)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onAboutClick = {
                    navController.navigate(Screen.About.route)
                },
                onDeleteAllNotes = {
                    settingsViewModel.deleteAllNotes()
                    updateFavorites()
                }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            val viewModel = remember { NoteDetailViewModel(noteRepository) }
            val templatesViewModel = remember { TemplatesViewModel(templateRepository) }
            
            LaunchedEffect(noteId) {
                if (noteId > 0) {
                    viewModel.loadNote(noteId)
                } else {
                    viewModel.resetState()
                }
            }
            
            NoteDetailScreen(
                viewModel = viewModel,
                templatesViewModel = templatesViewModel,
                noteId = noteId,
                onNavigateBack = {
                    // Обновляем данные при возвращении
                    updateFavorites()
                    navController.popBackStack()
                },
                onFavoriteToggle = { note ->
                    viewModel.toggleFavorite()
                    updateFavorites()
                }
            )
        }
    }
}

