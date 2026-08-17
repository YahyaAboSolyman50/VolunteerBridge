package com.example.volunteerbridge.view.nav_bottom.org.home

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
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.view.components.*
import com.example.volunteerbridge.viewmodelApi.NotViewModel
import com.example.volunteerbridge.viewmodelApi.OrganizationViewModel

@Composable
fun NotificationScreenOrg(
    notViewModel: NotViewModel,
    orgViewModel: OrganizationViewModel,
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme
    val notifications by notViewModel.notifications
    val orgModel by orgViewModel.currentOrganization
    val context = LocalContext.current

    // استخدام حالة التحميل الحقيقية من الـ ViewModel
    val isLoading by notViewModel.isLoading

    LaunchedEffect(orgModel?.id) {
        val orgId = orgModel?.id
        if (orgId != null && orgId != 0) {
            notViewModel.fetchNotifications(orgId)
        } else {
            Toast.makeText(context, context.getString(R.string.error_org_id_not_found), Toast.LENGTH_SHORT).show()
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
                        val orgId = orgModel?.id
                        if (orgId != null && orgId != 0) {
                            notViewModel.markAllAsRead(orgId)
                            Toast.makeText(context, context.getString(R.string.all_notifications_marked_read), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, context.getString(R.string.cannot_update_notifications_missing_id), Toast.LENGTH_SHORT).show()
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
                    NotificationCardShimmer()
                }
            }
            // إذا انتهى التحميل ولم تكن هناك إشعارات، اعرض رسالة "لا توجد إشعارات"
            else if (notifications.isEmpty()) {
                item { EmptyNotifications() }
            }
            // عرض قائمة الإشعارات الخاصة بالمنظمة في حال توفرها
            else {
                items(notifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onNotificationClick = { id ->
                            if (id.isNotEmpty()) {
                                notViewModel.markAsRead(id)
                            } else {
                                Toast.makeText(context, context.getString(R.string.invalid_notification_id), Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}