package com.example.volunteerbridge.model.classes.status

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class NotificationItem(
    val id: String = "",           // معرف الإشعار في Firestore (Document ID)
    val receiverId: String = "",   // الشخص الذي سيستلم الإشعار (UID) - الأهم للفلترة
    val senderId: String = "",     // الشخص الذي تسبب في الإشعار (اختياري)
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(), // نستخدم Long بدلاً من String للفرز الزمني
    val type: String = "info",     // لتحديد الأيقونة برمجياً (مثلاً: "applicant", "verify", "alert")
    val isRead: Boolean = false
)
