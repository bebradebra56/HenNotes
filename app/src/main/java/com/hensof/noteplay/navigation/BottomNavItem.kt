package com.hensof.noteplay.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        icon = Icons.Default.Home,
        title = "Home"
    )
    
    object Notes : BottomNavItem(
        route = Screen.Notes.route,
        icon = Icons.Default.Note,
        title = "Notes"
    )
    
    object Categories : BottomNavItem(
        route = Screen.Categories.route,
        icon = Icons.Default.Category,
        title = "Categories"
    )
    
    object Statistics : BottomNavItem(
        route = Screen.Statistics.route,
        icon = Icons.Default.BarChart,
        title = "Stats"
    )
    
    object Settings : BottomNavItem(
        route = Screen.Settings.route,
        icon = Icons.Default.Settings,
        title = "Settings"
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Notes,
//    BottomNavItem.Categories,
    BottomNavItem.Statistics,
    BottomNavItem.Settings
)

