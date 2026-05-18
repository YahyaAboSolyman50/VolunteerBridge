package com.example.volunteerbridge.view.nav_bottom.org.home


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.model.classes.NotificationModel
import com.example.volunteerbridge.view.functions.formatTimeAgo
import com.example.volunteerbridge.viewmodel.AuthViewModel
import com.example.volunteerbridge.viewmodel.NotViewModel
import com.example.volunteerbridge.viewmodel.OrgViewModel


@Composable
fun NotificationScreenOrg(notViewModel: NotViewModel,orgViewModel: OrgViewModel) {
    val colorScheme = MaterialTheme.colorScheme
    val notifications by notViewModel.notifications
    val orgModel by orgViewModel.currentOrgData

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
                    // إضافة وظيفة تحديد الكل كمقروء من الـ ViewModel
                    onMarkAllRead = { notViewModel.markAllAsRead(orgModel?.uid ?: "") },
                    hasNotifications = notifications.any { !it.isRead }
                )
            }

            if (notifications.isEmpty()) {
                item { EmptyNotifications() }
            } else {
                items(notifications) { notification ->
                    NotificationCard(
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
fun NotificationHeader(onMarkAllRead: () -> Unit, hasNotifications: Boolean) {
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
                text = "Notifications",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onBackground
            )
            if (hasNotifications) {
                Text(
                    text = "You have new updates",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.primary // لون التيل (Teal) المضيء
                )
            }
        }

        if (hasNotifications) {
            TextButton(onClick = onMarkAllRead) {
                Text(
                    text = "Mark all read",
                    fontSize = 12.sp,
                    color = colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationModel, onNotificationClick: (String) -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    val icon = notification.getIcon()
    val iconColor = notification.getColor() // هذا اللون يأتي عادة من الموديل (مثل الأحمر للتنبيه أو الأخضر للنجاح)
    val timeAgo = formatTimeAgo(notification.timestamp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onNotificationClick(notification.notificationId) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            // إذا لم يقرأ: خلفية مشتقة من لون الهوية، إذا قرأ: لون السطح العادي
            containerColor = if (!notification.isRead)
                colorScheme.primary.copy(alpha = 0.08f)
            else
                colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (!notification.isRead)
                colorScheme.primary.copy(alpha = 0.2f)
            else
                colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // دائرة الأيقونة
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (!notification.isRead) FontWeight.ExtraBold else FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2
                )
                Text(
                    text = timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurface.copy(alpha = 0.4f), // لون هادئ للوقت
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // نقطة التنبيه للإشعارات غير المقروءة
            if (!notification.isRead) {
                Surface(
                    modifier = Modifier.size(8.dp),
                    color = colorScheme.primary, // استخدام لون التطبيق الأساسي للنقطة
                    shape = CircleShape
                ) {}
            }
        }
    }
}

@Composable
fun EmptyNotifications() {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = colorScheme.onBackground.copy(alpha = 0.05f) // أيقونة شفافة جداً للخلفية
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your inbox is empty",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground.copy(alpha = 0.4f)
        )
        Text(
            text = "We'll notify you when something comes up",
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onBackground.copy(alpha = 0.3f)
        )
    }
}