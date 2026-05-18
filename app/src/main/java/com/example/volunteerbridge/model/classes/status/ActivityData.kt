package com.example.volunteerbridge.model.classes.status

data class ActivityData(
    val title: String,
    val time: Long,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconColor: androidx.compose.ui.graphics.Color,
    val type: String
)
