package com.example.volunteerbridge.model.classes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.firestore.Exclude
data class NotificationModel(
    val notificationId: String = "",
    val receiverId: Int = 0,
    val senderId: Int = 0,
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    @field:JvmField
    val isRead: Boolean = false,
    val type: String = "GENERAL"
) {

    @Exclude
    fun getIcon(): ImageVector {
        return when (type) {
            "APPLICATION" -> Icons.Default.Person
            "ACCEPTED" -> Icons.Default.CheckCircle
            "REJECTED" -> Icons.Default.Info
            "CLOSED" -> Icons.Default.Notifications
            else -> Icons.Default.Notifications
        }
    }
    @Exclude
    fun getColor(): Color {
        return when (type) {
            "APPLICATION" -> Color.Cyan
            "ACCEPTED" -> Color(0xFF4CAF50)
            "REJECTED" -> Color(0xFFFF4B4B)
            "CLOSED" -> Color.Gray
            else -> Color.Cyan
        }
    }
}