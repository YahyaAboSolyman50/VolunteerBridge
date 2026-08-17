package com.example.volunteerbridge.view.nav_bottom.org

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.data.model.response.ParticipationResponse
import com.example.volunteerbridge.model.classes.NotificationModel
import com.example.volunteerbridge.view.components.formatTimeAgo
import com.example.volunteerbridge.viewmodelApi.NotViewModel
import com.example.volunteerbridge.viewmodelApi.OrganizationViewModel
import com.example.volunteerbridge.viewmodelApi.ParticipationViewModel

@Composable
fun ApplicationsScreen(
    participationViewModel: ParticipationViewModel,
    organizationViewModel: OrganizationViewModel,
    notViewModel: NotViewModel
) {
    ApplicationsScreenDesign(participationViewModel, organizationViewModel, notViewModel)
}

@Composable
fun ApplicationsScreenDesign(
    participationViewModel: ParticipationViewModel,
    organizationViewModel: OrganizationViewModel,
    notViewModel: NotViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabPending = stringResource(R.string.tab_pending)
    val tabApproved = stringResource(R.string.tab_approved)
    val tabCompleted = stringResource(R.string.tab_completed) // تأكد من توفير النص في strings.xml
    val tabRejected = stringResource(R.string.tab_rejected)
    val tabs = listOf(tabPending, tabApproved, tabCompleted, tabRejected)

    val applications by participationViewModel.applications.collectAsState()
    val orgModel by organizationViewModel.currentOrganization
    val isLoading by participationViewModel.isLoading.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        participationViewModel.fetchOrganizationApplications()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 40.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app),
                        color = colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            text = stringResource(R.string.total_applications, applications.size),
                            color = colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = colorScheme.primary,
                    edgePadding = 24.dp,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        val count = applications.count {
                            when (index) {
                                1 -> it.status.equals("Approved", ignoreCase = true) || it.status.equals("Accepted", ignoreCase = true)
                                2 -> it.status.equals("Completed", ignoreCase = true)
                                0 -> it.status.equals("Pending", ignoreCase = true)
                                3 -> it.status.equals("Rejected", ignoreCase = true)
                                else -> false
                            }
                        }
                        val isSelected = selectedTab == index

                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = title,
                                        color = if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )

                                    if (count > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = if (isSelected) colorScheme.primary.copy(alpha = 0.2f) else colorScheme.onSurface.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = count.toString(),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontSize = 10.sp,
                                                color = if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.6f),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            val currentTabTitle = tabs[selectedTab]
            val filteredApps = applications.filter {
                when (selectedTab) {
                    1 -> it.status.equals("Approved", ignoreCase = true) || it.status.equals("Accepted", ignoreCase = true)
                    2 -> it.status.equals("Completed", ignoreCase = true)
                    0 -> it.status.equals("Pending", ignoreCase = true)
                    3 -> it.status.equals("Rejected", ignoreCase = true)
                    else -> false
                }
            }

            if (filteredApps.isEmpty() && !isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_requests_format, currentTabTitle),
                            color = colorScheme.onSurface.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(filteredApps.size) { index ->
                    val app = filteredApps[index]
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        ApplicantRequestCard(
                            app = app,
                            orgId = orgModel?.id ?: 0,
                            participationViewModel = participationViewModel,
                            notViewModel = notViewModel
                        )
                    }
                }
            }
        }

        if (isLoading && applications.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorScheme.primary
            )
        }
    }
}

@Composable
fun ApplicantRequestCard(
    app: ParticipationResponse,
    orgId: Int,
    participationViewModel: ParticipationViewModel,
    notViewModel: NotViewModel
) {
    val colorScheme = MaterialTheme.colorScheme
    var actionLoading by remember { mutableStateOf(false) }

    val isPending = app.status.equals("Pending", ignoreCase = true)
    val isAccepted = app.status.equals("Accepted", ignoreCase = true) || app.status.equals("Approved", ignoreCase = true)
    val isCompleted = app.status.equals("Completed", ignoreCase = true)
    val isRejected = app.status.equals("Rejected", ignoreCase = true)

    val containerColor = colorScheme.surface

    val badgeColor = when {
        isCompleted -> Color(0xFF2196F3) // لون أزرق للمكتمل
        isAccepted -> Color(0xFF4CAF50)
        isRejected -> Color(0xFFF44336)
        else -> Color(0xFFFF9800)
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isPending) 2.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = when {
                                isCompleted -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                isAccepted -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                isRejected -> colorScheme.error.copy(alpha = 0.2f)
                                else -> colorScheme.primary.copy(alpha = 0.1f)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isCompleted -> Icons.Default.Done
                            isAccepted -> Icons.Default.CheckCircle
                            isRejected -> Icons.Default.Close
                            else -> Icons.Default.Info
                        },
                        contentDescription = null,
                        tint = when {
                            isCompleted -> Color(0xFF2196F3)
                            isAccepted -> Color(0xFF4CAF50)
                            isRejected -> colorScheme.error
                            else -> colorScheme.primary
                        }
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    app.studentName?.let {
                        Text(
                            text = stringResource(R.string.volunteer_name_format, it),
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = try {
                            val format = java.text.SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss",
                                java.util.Locale.getDefault()
                            )
                            val date = format.parse(app.joinedAt)
                            formatTimeAgo(date?.time ?: 0L)
                        } catch (e: Exception) {
                            app.joinedAt
                        },
                        color = colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isPending -> {
                    if (actionLoading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = colorScheme.primary
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    actionLoading = true
                                    participationViewModel.rejectApplication(app.id) { success ->
                                        actionLoading = false
                                        if (success) {
                                            notViewModel.sendNotification(
                                                NotificationModel(
                                                    receiverId = app.user,
                                                    senderId = orgId,
                                                    title = "تحديث حالة الطلب",
                                                    message = "نأسف، تم رفض طلب انضمامك إلى الفرصة التطوعية ${app.activityTitle}."
                                                )
                                            )
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorScheme.errorContainer,
                                    contentColor = colorScheme.error
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(stringResource(R.string.reject))
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    actionLoading = true
                                    participationViewModel.approveApplication(app.id) { success ->
                                        actionLoading = false
                                        if (success) {
                                            notViewModel.sendNotification(
                                                NotificationModel(
                                                    receiverId = app.user,
                                                    senderId = orgId,
                                                    title = "تحديث حالة الطلب",
                                                    message = "لقد تم قبولك في فرصة, ${app.activityTitle}"
                                                )
                                            )
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorScheme.primary,
                                    contentColor = colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(stringResource(R.string.accept), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                isAccepted -> {
                    // إذا كانت مقبولة، نعرض زر إتمام المشاركة (Complete) بجانب الشارة أو مكانها
                    if (actionLoading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = colorScheme.primary
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = stringResource(R.string.accepted_status),
                                    color = Color(0xFF388E3C),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = {
                                    actionLoading = true
                                    // افترض أن الـ ViewModel يحتوي على دالة completeParticipation
                                    participationViewModel.completeParticipation(app.id) { success ->
                                        actionLoading = false
                                        if (success) {
                                            notViewModel.sendNotification(
                                                NotificationModel(
                                                    receiverId = app.user,
                                                    senderId = orgId,
                                                    title = "إتمام الفرصة",
                                                    message = "تهانينا، تم إتمام مشاركتك في فرصة ${app.activityTitle} بنجاح."
                                                )
                                            )
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2196F3),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("إتمام (Complete)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
                else -> {
                    // الحالات المنتهية الأخرى (مكتمل أو مرفوض)
                    Surface(
                        color = when {
                            isCompleted -> Color(0xFF2196F3).copy(alpha = 0.15f)
                            else -> colorScheme.error.copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = when {
                                isCompleted -> "مكتمل (Completed)" // أو استبدلها بـ stringResource لو متوفرة
                                else -> stringResource(R.string.rejected_status)
                            },
                            color = when {
                                isCompleted -> Color(0xFF1976D2)
                                else -> colorScheme.error
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}