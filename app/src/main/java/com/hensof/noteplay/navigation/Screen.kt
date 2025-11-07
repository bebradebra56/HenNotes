package com.hensof.noteplay.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Notes : Screen("notes")
    object Categories : Screen("categories")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object About : Screen("about")
    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Long = 0) = "note_detail/$noteId"
    }
}

