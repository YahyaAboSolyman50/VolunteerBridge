package com.example.volunteerbridge.view.nav_bottom.org.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.model.classes.OpportunityModel
import com.example.volunteerbridge.view.functions.formatTimeAgo
import com.example.volunteerbridge.viewmodel.AuthViewModel
import com.example.volunteerbridge.viewmodel.NotViewModel
import com.example.volunteerbridge.viewmodel.OpportunityViewModel
import com.example.volunteerbridge.viewmodel.OrgViewModel

@Composable
fun OrganizationHomeContent(
    orgViewModel: OrgViewModel,
    oppViewModel: OpportunityViewModel,
    notViewModel: NotViewModel,
    onNotificationClick: () -> Unit,
    onTopPreClick: (String) -> Unit
) {
    val orgModel by orgViewModel.currentOrgData
    val oppList by oppViewModel.orgOpp
    val isVerified = orgModel?.verified ?: false
    val hasUnread by notViewModel.hasNewNotifications
    val bestOpp = oppList.maxByOrNull { it.applicantsCount }

    val colorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background), // استخدام الخلفية الموحدة للتطبيق
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            HeaderSection(
                name = orgModel?.nameOrg ?: "Organization",
                isVerified = isVerified,
                accentColor = colorScheme.primary,
                hasNotifications = hasUnread,
                onNotifyClick = onNotificationClick
            )
        }

        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                // الإحصائيات (Stat Boxes)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatBox(
                        modifier = Modifier.weight(1f),
                        title = "Active Ops",
                        value = "${oppList.size}",
                        icon = Icons.Default.DateRange,
                        accentColor = colorScheme.primary
                    )
                    StatBox(
                        modifier = Modifier.weight(1f),
                        title = "Applicants",
                        value = "${oppList.sumOf { it.applicantsCount }}",
                        icon = Icons.Default.AccountBox,
                        accentColor = colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Top Performing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground // يتغير حسب الثيم
                )
                Spacer(modifier = Modifier.height(12.dp))
                TopPerformingOpp(
                    bestOpp,
                    colorScheme.primary,
                    onTopPreClick = {
                        bestOpp?.id?.let { id -> onTopPreClick(id) }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    )
                    TextButton(onClick = { onNotificationClick() }) {
                        Text("View All", color = colorScheme.primary, fontSize = 14.sp)
                    }
                }

                RecentActivityList(notViewModel)
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
                "Hello, $name 👋",
                color = onBg,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                // في حالة التوثيق نستخدم اللون الأساسي، وإلا نستخدم لون خطأ (Error)
                color = if (isVerified) accentColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(50.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (isVerified) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isVerified) accentColor else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isVerified) "Verified Organization" else "Pending Verification",
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
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface // CardBgDark أو CardBgLight
        ),
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
fun TopPerformingOpp(opp: OpportunityModel?, accentColor: Color, onTopPreClick: (String) -> Unit) {
    if (opp == null) return
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTopPreClick(opp.id) },
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
                    opp.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorScheme.onSurface
                )
                Text(
                    "${opp.applicantsCount} volunteers applied",
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
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
            Text(
                time,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun RecentActivityList(notViewModel: NotViewModel) {
    val activities by notViewModel.notifications
    val recentActivities = activities.take(5)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (activities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No recent activity yet", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            recentActivities.forEach { notification ->
                ActivityItem(
                    title = notification.title + ": " + notification.message,
                    time = formatTimeAgo(notification.timestamp),
                    icon = notification.getIcon(),
                    iconColor = notification.getColor()
                )
            }
        }
    }
}

