package com.hensof.noteplay.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Spa
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.hensof.noteplay.ui.theme.EggColor
import com.hensof.noteplay.ui.theme.FeedColor
import com.hensof.noteplay.ui.theme.FinanceColor
import com.hensof.noteplay.ui.theme.FreeNotesColor
import com.hensof.noteplay.ui.theme.HealthColor

enum class NoteCategory(
    val displayName: String,
    val icon: ImageVector,
    val color: Color,
    val emoji: String
) {
    EGG_PRODUCTION(
        displayName = "Egg Production",
        icon = Icons.Default.Egg,
        color = EggColor,
        emoji = "🥚"
    ),
    FEED_NUTRITION(
        displayName = "Feed & Nutrition",
        icon = Icons.Default.Spa,
        color = FeedColor,
        emoji = "🌾"
    ),
    HEALTH(
        displayName = "Health",
        icon = Icons.Default.HealthAndSafety,
        color = HealthColor,
        emoji = "🐥"
    ),
    FINANCE(
        displayName = "Finance",
        icon = Icons.Default.AttachMoney,
        color = FinanceColor,
        emoji = "💰"
    ),
    FREE_NOTES(
        displayName = "Free Notes",
        icon = Icons.Default.Favorite,
        color = FreeNotesColor,
        emoji = "📝"
    )
}

