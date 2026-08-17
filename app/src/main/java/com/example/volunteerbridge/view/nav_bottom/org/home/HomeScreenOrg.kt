package com.example.volunteerbridge.view.nav_bottom.org.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.data.model.TokenManager
import com.example.volunteerbridge.data.model.response.ActivityResponse
import com.example.volunteerbridge.view.components.formatTimeAgo
import com.example.volunteerbridge.viewmodelApi.NotViewModel
import com.example.volunteerbridge.viewmodelApi.ActivityViewModel
import com.example.volunteerbridge.viewmodelApi.AuthViewModelApi
import com.example.volunteerbridge.viewmodelApi.OrganizationViewModel

@Composable
fun OrganizationHomeContent(
    organizationViewModel: OrganizationViewModel,
    activityViewModel: ActivityViewModel,
    authViewModelApi: AuthViewModelApi,
    notViewModel: NotViewModel,
    onNotificationClick: () -> Unit,
    onTopPreClick: (String) -> Unit
) {
    val token = remember { TokenManager.getToken() ?: "" }
    val currentOrg by organizationViewModel.currentOrganization

    val myActivities by activityViewModel.myActivities.collectAsState()

    val defaultOrgName = stringResource(R.string.organization_default_name)
    val orgName = currentOrg?.name ?: defaultOrgName
    val isVerified = currentOrg?.verified ?: true

    val hasUnread by notViewModel.hasNewNotifications
    val notificationsList by notViewModel.notifications

    val bestOpp = myActivities.maxByOrNull { it.id ?: 0 }
    LaunchedEffect(Unit) {
        organizationViewModel.fetchMyOrganization()
        activityViewModel.loadMyOrganizationActivities()

    }

    LaunchedEffect(currentOrg?.id) {
        currentOrg?.id?.let { orgId ->
//            activityViewModel.loadMyOrganizationActivities()
            notViewModel.fetchNotifications(orgId)
        }
    }

    OrganizationHomeDesign(
        orgName = orgName,
        isVerified = isVerified,
        hasUnreadNotifications = hasUnread,
        myActivities = myActivities,
        bestOpp = bestOpp,
        notifications = notificationsList,
        onNotificationClick = onNotificationClick,
        onTopPreClick = onTopPreClick
    )
}

@Composable
fun OrganizationHomeDesign(
    orgName: String,
    isVerified: Boolean,
    hasUnreadNotifications: Boolean,
    myActivities: List<ActivityResponse>,
    bestOpp: ActivityResponse?,
    notifications: List<com.example.volunteerbridge.model.classes.NotificationModel>,
    onNotificationClick: () -> Unit,
    onTopPreClick: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            HeaderSection(
                name = orgName,
                isVerified = isVerified,
                accentColor = colorScheme.primary,
                hasNotifications = hasUnreadNotifications,
                onNotifyClick = onNotificationClick
            )
        }

        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatBox(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.stat_active_ops),
                        value = "${myActivities.size}",
                        icon = Icons.Default.DateRange,
                        accentColor = colorScheme.primary
                    )
                    StatBox(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.stat_applicants),
                        value = "${myActivities.sumOf { it.applicantsCount ?: 0L }}",
                        icon = Icons.Default.AccountBox,
                        accentColor = colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.top_performing_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                TopPerformingOppApi(
                    opp = bestOpp,
                    accentColor = colorScheme.primary,
                    onTopPreClick = { id -> onTopPreClick(id) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.recent_activity_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    )
                    TextButton(onClick = { onNotificationClick() }) {
                        Text(stringResource(R.string.view_all), color = colorScheme.primary, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                RecentActivityList(notifications = notifications)
            }
        }
    }
}

@Composable
fun HeaderSection(
    name: String,
    isVerified: Boolean,
    accentColor: Color,
    hasNotifications: Boolean,
    onNotifyClick: () -> Unit
) {
    val onBg = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(65.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.1f))
                .border(2.dp, accentColor.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(35.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.hello_user_greeting, name),
                color = onBg,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = if (isVerified) accentColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(50.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isVerified) accentColor else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isVerified) stringResource(R.string.status_verified_org) else stringResource(R.string.status_pending_verification),
                        color = if (isVerified) accentColor else MaterialTheme.colorScheme.error,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        IconButton(onClick = onNotifyClick) {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = onBg,
                    modifier = Modifier.size(28.dp)
                )
                if (hasNotifications) {
                    Surface(
                        color = MaterialTheme.colorScheme.error,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                    ) {}
                }
            }
        }
    }
}

@Composable
fun StatBox(
    modifier: Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            Text(
                title,
                fontSize = 13.sp,
                color = colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun TopPerformingOppApi(
    opp: ActivityResponse?,
    accentColor: Color,
    onTopPreClick: (String) -> Unit
) {
    if (opp == null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Text(
                text = stringResource(R.string.no_opportunities_posted),
                modifier = Modifier.padding(20.dp),
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        return
    }

    val colorScheme = MaterialTheme.colorScheme
    val activeStatusText = stringResource(R.string.status_active_label)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTopPreClick(opp.id.toString()) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(Color(0xFFFFD700).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = opp.title.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorScheme.onSurface
                )
                Text(
                    text = "Status: ${opp.status ?: activeStatusText}",
                    fontSize = 13.sp,
                    color = colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = accentColor.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun RecentActivityList(notifications: List<com.example.volunteerbridge.model.classes.NotificationModel>) {
    val topRecentNotifications = notifications.take(3)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (topRecentNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_recent_activity), color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            topRecentNotifications.forEach { notification ->
                ActivityItem(
                    title = "${notification.title}: ${notification.message}",
                    time = formatTimeAgo(notification.timestamp),
                    icon = notification.getIcon(),
                    iconColor = notification.getColor()
                )
            }
        }
    }
}

@Composable
fun ActivityItem(title: String, time: String, icon: ImageVector, iconColor: Color) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface,
                maxLines = 2
            )
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}