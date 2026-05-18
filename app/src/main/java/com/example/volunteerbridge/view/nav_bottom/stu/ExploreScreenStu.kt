package com.example.volunteerbridge.view.nav_bottom.stu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.model.classes.NotificationModel
import com.example.volunteerbridge.view.functions.formatTimeAgo
import com.example.volunteerbridge.viewmodel.NotViewModel
import com.example.volunteerbridge.viewmodel.StudentViewModel

@Composable
fun NotificationScreenStu(
    notViewModel: NotViewModel,
    stuViewModel: StudentViewModel
) {
    val colorScheme = MaterialTheme.colorScheme
    // استماع للإشعارات الخاصة بالطالب الحالي
    val notifications by notViewModel.notifications
    val userModel by stuViewModel.currentUserData

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // الهيدر الأنيق المخصص للطالب
            item {
                NotificationHeaderStu(
                    onMarkAllRead = { notViewModel.markAllAsRead(userModel?.uid ?: "") },
                    hasNotifications = notifications.any { !it.isRead }
                )
            }

            if (notifications.isEmpty()) {
                item { EmptyNotificationsStu() }
            } else {
                // استخدام items مع معرف فريد يعطي سلاسة تامة في الأنيميشن عند التحديث
                items(notifications, key = { it.notificationId }) { notification ->
                    NotificationCardStu(
                        notification = notification,
                        onNotificationClick = { id ->
                            if (id.isNotEmpty()) {
                                notViewModel.markAsRead(id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationHeaderStu(onMarkAllRead: () -> Unit, hasNotifications: Boolean) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Updates & Alerts", // اسم حركي ومناسب للطلاب بدلاً من كلمة إشعارات التقليدية
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onBackground
            )
            AnimatedVisibility(visible = hasNotifications, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = "Tap on unread alerts to clear them",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.primary
                )
            }
        }

        if (hasNotifications) {
            TextButton(
                onClick = onMarkAllRead,
                colors = ButtonDefaults.textButtonColors(contentColor = colorScheme.primary)
            ) {
                Text(
                    text = "Mark all read",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun NotificationCardStu(notification: NotificationModel, onNotificationClick: (String) -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val timeAgo = formatTimeAgo(notification.timestamp)

    // ✨ لمسة جمالية 1: ذكاء الأيقونة والألوان بناءً على حالة قبول أو رفض الطالب في الفرصة التطوعية
    val (icon, iconColor) =
        when {
        notification.title.lowercase().contains("accept") || notification.message.lowercase().contains("accept") -> {
            Icons.Default.CheckCircle to Color(0xFF4CAF50) // أخضر مبهج للقبول
        }
        notification.title.lowercase().contains("reject") || notification.message.lowercase().contains("reject") -> {
            Icons.Default.Close to Color(0xFFF44336) // أحمر دلالي للرفض
        }
        else -> {
            Icons.Default.Info to colorScheme.primary // لون التطبيق الأساسي للإشعارات الإدارية والتنبيهات العامة
        }
    }

    ElevatedCard(
        modifier = Modifier
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = if (!notification.isRead)
                        colorScheme.primary.copy(alpha = 0.15f)
                    else
                        colorScheme.outline.copy(alpha = 0.05f)
                )
            )
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onNotificationClick(notification.notificationId) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            // الحفاظ على تمييز الباسل بين المقروء وغير المقروء بنسبة شفافية Primary احترافية
            containerColor = if (!notification.isRead)
                colorScheme.primary.copy(alpha = 0.06f)
            else
                colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (!notification.isRead) 2.dp else 1.dp
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // دائرة الأيقونة الخلفية الملونة والمريحة للعين
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (!notification.isRead) FontWeight.ExtraBold else FontWeight.Bold,
                    color = colorScheme.onSurface,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurface.copy(alpha = 0.65f),
                    maxLines = 3,
                    lineHeight = 18.sp
                )

                Text(
                    text = timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // نقطة التنبيه المضيئة للإشعارات غير المقروءة متوافقة مع ثيم الطالب البصري
            if (!notification.isRead) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    modifier = Modifier.size(8.dp),
                    color = colorScheme.primary,
                    shape = CircleShape
                ) {}
            }
        }
    }
}

@Composable
fun EmptyNotificationsStu() {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 140.dp, start = 32.dp, end = 32.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ✨ لمسة جمالية 2: أيقونة هادئة مع نصوص تشجيعية مخصصة للطلاب تزيد من جمال الـ UX
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(45.dp),
                tint = colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "All caught up!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "When an organization updates your request, you'll see it here.",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onBackground.copy(alpha = 0.4f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}