package com.example.volunteerbridge.view.nav_bottom.stu.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.volunteerbridge.view.components.*
import com.example.volunteerbridge.viewmodelApi.NotViewModel
import com.example.volunteerbridge.viewmodelApi.StudentViewModel

@Composable
fun NotificationScreenStu(
    notViewModel: NotViewModel,
    studentViewModel: StudentViewModel,
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme
    val notifications by notViewModel.notifications
    val studentModel by studentViewModel.currentUserData
    val context = LocalContext.current

    // جلب حالة التحميل الحقيقية من الـ ViewModel
    val isLoading by notViewModel.isLoading

    LaunchedEffect(studentModel) {
        val studentId = studentModel?.id
        if (studentId != null && studentId != 0) {
            Log.d("Aaaaa", "NotificationScreenStu: no null")
            notViewModel.fetchNotifications(studentId)
        } else {
            Log.d("Aaaaa", "NotificationScreenStu: no null")

            // إذا لم يكن المعرف متوفراً بعد، نوقف التحميل مؤقتاً أو نمرر 0
            notViewModel.fetchNotifications(0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                NotificationHeader(
                    onMarkAllRead = {
                        val studentId = studentModel?.id
                        if (studentId != null && studentId != 0) {
                            notViewModel.markAllAsRead(studentId)
                            Toast.makeText(context, "All notifications marked as read", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Cannot update notifications: ID is missing", Toast.LENGTH_SHORT).show()
                        }
                    },
                    hasNotifications = notifications.any { !it.isRead }
                ){
                    navController.popBackStack()
                }
            }

            // عرض تأثيرات الـ Shimmer فقط أثناء جلب البيانات لأول مرة
            if (isLoading) {
                items(5) {
                    EmptyNotifications()
                }
            }
            // إذا انتهى التحميل ولم تكن هناك إشعارات، اعرض رسالة "لا توجد إشعارات"
            else if (notifications.isEmpty()) {
                item { EmptyNotifications() }
            }
            // عرض قائمة الإشعارات في حال توفرها
            else {
                items(notifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onNotificationClick = { id ->
                            if (id.isNotEmpty()) {
                                notViewModel.markAsRead(id)
                            } else {
                                Toast.makeText(context, "Invalid notification ID", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}