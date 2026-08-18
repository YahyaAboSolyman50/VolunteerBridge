package com.example.volunteerbridge.view.nav_bottom.stu.home

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.data.model.response.ActivityResponse
import com.example.volunteerbridge.model.classes.NotificationModel
import com.example.volunteerbridge.viewmodelApi.ActivityViewModel
import com.example.volunteerbridge.viewmodelApi.AuthViewModelApi
import com.example.volunteerbridge.viewmodelApi.NotViewModel
import com.example.volunteerbridge.viewmodelApi.StudentViewModel
import com.valentinilk.shimmer.shimmer

// شاشة تفاصيل الفرصة التطوعية لعرض معلومات الفرصة وإتاحة إمكانية التقديم لها
@Composable
fun OpportunityDetailScreen(
    opportunityId: Int,
    oppViewModel: ActivityViewModel,
    stuViewModel: StudentViewModel,
    authViewModelApi: AuthViewModelApi,
    notificationViewModel: NotViewModel,
    onBackClick: () -> Unit
) {
    LaunchedEffect(opportunityId) {
        oppViewModel.getActivityById(opportunityId)
    }

    val selectedOpportunity by oppViewModel.selectedActivity.collectAsState()
    val studentToken = remember { authViewModelApi.getSavedToken() ?: "" }
    val stuModel by stuViewModel.currentUserData
    val context = LocalContext.current

    val joinStatus by oppViewModel.joinStatus.collectAsState()
    val isJoinLoading by oppViewModel.isJoinLoading.collectAsState()

    val successJoinMsg = stringResource(R.string.success_joined)
    val failJoinMsg = stringResource(R.string.fail_joined)
    val notifTitle = stringResource(R.string.new_application_title)
    val notifMsgFormat = stringResource(R.string.new_application_message_format)
    val tokenNotFoundMsg = stringResource(R.string.token_not_found)

    LaunchedEffect(joinStatus) {
        when (joinStatus) {
            true -> {
                Toast.makeText(context, successJoinMsg, Toast.LENGTH_SHORT).show()
            }
            false -> {
                Toast.makeText(context, failJoinMsg, Toast.LENGTH_SHORT).show()
            }
            null -> {}
        }
    }

    DetailedDesign(
        opp = selectedOpportunity,
        isJoinLoading = isJoinLoading,
        joinStatus = joinStatus,
        onApplyClick = {
            selectedOpportunity?.let { opp ->
                opp.id?.let { id ->
                    if (studentToken.isNotEmpty()) {
                        val orgId = opp.organization
                        val studentId = stuModel?.id
                        val oppTitle = opp.title ?: ""

                        oppViewModel.joinActivity(
                            activityId = id,
                            onSuccess = {
                                if (orgId != null && orgId != 0) {
                                    val notification = NotificationModel(
                                        receiverId = orgId,
                                        senderId = studentId ?: 0,
                                        title = notifTitle,
                                        message = String.format(notifMsgFormat, oppTitle),
                                        type = "APPLICATION"
                                    )
                                    notificationViewModel.sendNotification(
                                        notification,
                                        onSuccess = {
                                            onBackClick()
                                        }
                                    )
                                } else {
                                    onBackClick()
                                }
                            }
                        )
                    } else {
                        Toast.makeText(context, tokenNotFoundMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        },
        onBackClick = onBackClick,
        activityViewModel = oppViewModel
    )
}

// التصميم التفصيلي لشاشة عرض الفرصة التطوعية
@Composable
private fun DetailedDesign(
    opp: ActivityResponse?,
    isJoinLoading: Boolean,
    joinStatus: Boolean?,
    onApplyClick: () -> Unit,
    onBackClick: () -> Unit,
    activityViewModel: ActivityViewModel
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background

    val myJoinedIds by activityViewModel.myParticipationsIds.collectAsState()
    val isAlreadyJoined = opp?.let { myJoinedIds.contains(it.id) } ?: false

    val detailedStatus = opp?.status?.trim() ?: ""
    val isActive = detailedStatus.equals("Active", ignoreCase = true) ||
            detailedStatus.equals("Open", ignoreCase = true)

    val appsClosedText = stringResource(R.string.apps_closed)
    val alreadyJoinedText = stringResource(R.string.already_joined)
    val quickApplyText = stringResource(R.string.quick_apply)

    val buttonText = when {
        !isActive -> appsClosedText
        isAlreadyJoined -> alreadyJoinedText
        else -> quickApplyText
    }
    val isButtonEnabled = isActive && !isAlreadyJoined && !isJoinLoading

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (isSystemInDarkTheme()) {
                            listOf(backgroundColor, Color(0xFF0A1A33))
                        } else {
                            listOf(backgroundColor, Color(0xFFF1F5F9))
                        }
                    )
                ),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back_desc),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    if (opp == null) {
                        Column(modifier = Modifier.fillMaxWidth().shimmer()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                            Spacer(Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.4f)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                            Spacer(Modifier.height(16.dp))
                            Row {
                                Box(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = opp.title.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.organization_prefix, opp.organization ?: 0) ,
                                style = MaterialTheme.typography.titleMedium,
                                color = primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = opp.category ?: stringResource(R.string.general_category),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(8.dp))

                            Surface(
                                color = if (isActive) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = detailedStatus.ifEmpty { stringResource(R.string.na_status) },
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isActive) Color(0xFF4CAF50) else Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    if (opp == null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().shimmer(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val locationLabel = stringResource(R.string.label_location)
                            opp.location?.let { InfoCard(Modifier.weight(1f), locationLabel, it, Icons.Default.LocationOn) }

                            val hoursLabel = stringResource(R.string.label_hours)
                            val hoursValue = stringResource(R.string.hours_format, opp.hours ?: 0)
                            InfoCard(Modifier.weight(1f), hoursLabel, hoursValue, Icons.Default.Star)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val limit = opp.volunteerLimit ?: 0
                            val applicants = opp.applicantsCount ?: 0
                            val seatsLeft = (limit - applicants).coerceAtLeast(0)

                            val seatsLabel = stringResource(R.string.label_seats_left)
                            val seatsValue = stringResource(R.string.seats_format, seatsLeft, limit)
                            InfoCard(Modifier.weight(1f), seatsLabel, seatsValue, Icons.Default.AccountCircle)

                            val deadlineLabel = stringResource(R.string.label_deadline)
                            val deadlineValue = opp.registrationDeadline ?: opp.endDate ?: stringResource(R.string.na_status)
                            InfoCard(Modifier.weight(1f), deadlineLabel, deadlineValue, Icons.Default.DateRange)
                        }
                    }
                }
            }

            item {
                SectionTitle(stringResource(R.string.section_description))
                if (opp == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .shimmer()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                } else {
                    val noDesc = stringResource(R.string.no_description)
                    Text(
                        text = opp.description?.ifEmpty { noDesc } ?: noDesc,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        lineHeight = 22.sp
                    )
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shadowElevation = 20.dp
        ) {
            Button(
                onClick = onApplyClick,
                enabled = opp != null && isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive && !isAlreadyJoined) MaterialTheme.colorScheme.primary else Color.Gray
                )
            ) {
                if (isJoinLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    val loadingText = stringResource(R.string.loading_text)
                    Text(text = if (opp == null) loadingText else buttonText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// بطاقة لعرض معلومة محددة مثل الموقع أو الساعات أو المقاعد المتاحة
@Composable
fun InfoCard(modifier: Modifier, label: String, value: String, icon: ImageVector) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

// عنصر لعنوان الأقسام المختلفة داخل شاشة التفاصيل
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}