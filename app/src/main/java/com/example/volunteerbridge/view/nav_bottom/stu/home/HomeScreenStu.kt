package com.example.volunteerbridge.view.nav_bottom.stu.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.model.classes.SubClasses
import com.example.volunteerbridge.data.model.response.ActivityResponse
import com.example.volunteerbridge.model.classes.Destination
import com.example.volunteerbridge.model.classes.NotificationModel
import com.example.volunteerbridge.viewmodelApi.ActivityViewModel
import com.example.volunteerbridge.viewmodelApi.AuthViewModelApi
import com.example.volunteerbridge.viewmodelApi.NotViewModel
import com.example.volunteerbridge.viewmodelApi.StudentViewModel
import com.valentinilk.shimmer.shimmer

/**
 * دالة التغليف الأساسية لعرض محتوى شاشة الطالب الرئيسية.
 */
@Composable
fun StudentHomeContent(
    activityViewModel: ActivityViewModel,
    authViewModelApi: AuthViewModelApi,
    studentViewModel: StudentViewModel,
    notViewModel: NotViewModel,
    navController: NavController,
    token: String
) {
    StudentHomeDesign(
        activityViewModel,
        authViewModelApi,
        studentViewModel,
        notViewModel,
        navController,
        token
    )
}

/**
 * التصميم الرئيسي لشاشة الطالب ويشمل: شريط الترحيب، بطاقة الساعات، وعنوان الفرص مع زر العرض الشامل وقائمة الفرص.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeDesign(
    activityViewModel: ActivityViewModel,
    authViewModelApi: AuthViewModelApi,
    studentViewModel: StudentViewModel,
    notViewModel: NotViewModel,
    navController: NavController,
    token: String
) {
    val context = LocalContext.current

    // استرجاع بيانات المستخدم والرمز التعريفي
    val userProfile by studentViewModel.currentUserData
    val studentToken = authViewModelApi.getSavedToken() ?: "YOUR_TOKEN"

    // مراقبة حالات الأنشطة والتحميل
    val activityList by activityViewModel.activities.collectAsState(initial = emptyList())
    val isLoading by activityViewModel.isLoading.collectAsState()

    val isJoinLoading by activityViewModel.isJoinLoading.collectAsState(initial = false)
    val selectedActivityId by activityViewModel.selectedActivityId.collectAsState(initial = null)

    // حساب نسبة الساعات المنجزة للمستخدم بشكل آمن
    val currentHours = userProfile?.totalCompletedHours?.toIntOrNull() ?: 0
    val progressPercentageString = userProfile?.completionPercentage?.replace("%", "")?.trim() ?: "0"
    val progressValue = (progressPercentageString.toFloatOrNull() ?: 0f) / 100f

    // تحميل الأنشطة عند فتح الشاشة لأول مرة
    LaunchedEffect(Unit) {
        activityViewModel.loadMyParticipations()
        activityViewModel.loadActivities()
    }

    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()

    // دالة تحديث بيانات الشاشة عند السحب لأسفل
    val refreshAllData: () -> Unit = {
        if (token.isNotEmpty()) {
            isRefreshing = true
            activityViewModel.loadScreenData {
                isRefreshing = false
                Toast.makeText(context, context.getString(R.string.data_updated_successfully), Toast.LENGTH_SHORT).show()
            }
        } else {
            isRefreshing = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = refreshAllData,
            state = pullRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // 1. قسم الترحيب وأزرار الإشعارات والبروفايل
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (!userProfile?.name.isNullOrEmpty())
                                    stringResource(R.string.welcome_message, userProfile?.name.orEmpty())
                                else
                                    stringResource(R.string.welcome_default),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 17.sp
                            )
                            Text(
                                text = stringResource(R.string.ready_to_impact),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // زر الإشعارات
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        navController.navigate(SubClasses.SubClassesOrg.Notification.route)
                                    }
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(8.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // أيقونة البروفايل الشخصي
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .clickable { navController.navigate(Destination.Student.Profile.route) }
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(8.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 2. بطاقة عرض ساعات التطوع ومؤشر الإنجاز الدائري
                item {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.total_volunteer_hours),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = stringResource(R.string.hours_unit, currentHours),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { progressValue.coerceIn(0f, 1f) },
                                    modifier = Modifier.size(65.dp),
                                    strokeWidth = 6.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Text(
                                    text = "${(progressValue * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // 3. عنوان الفرص المتاحة مع زر "عرض الكل" القابل للنقر بجانبه
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.available_opportunities),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = stringResource(R.string.viewAll),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    navController.navigate(Destination.Student.AllOpp.route)
                                }
                                .padding(4.dp)
                        )
                    }
                }

                // 4. عرض قائمة الأنشطة التطوعية (أول 5 فرص فقط) أو عناصر التحميل (Shimmer)
                if (isLoading) {
                    items(3) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .shimmer(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }
                } else {
                    activityList?.let { list ->
                        val limitedList = list.take(5)

                        if (limitedList.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_opportunities_found),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(limitedList.size) { index ->
                                val opp = limitedList[index]
                                val isThisCardLoading =
                                    isJoinLoading && selectedActivityId == opp.id

                                OpportunityCard(
                                    opp = opp,
                                    isThisButtonLoading = isThisCardLoading,
                                    activityViewModel = activityViewModel,
                                    onCardClick = { id ->
                                        activityViewModel.selectActivity(opp)
                                        navController.navigate("${SubClasses.SubClassesStu.OppDetail.route}/$id")
                                    },
                                    onApplyClick = {
                                        opp.id?.let { id ->
                                            activityViewModel.joinActivity(
                                                activityId = id
                                            ) {
                                                Toast.makeText(
                                                    context,
                                                    context.getString(R.string.successfully_joined_opportunity),
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                val orgId = opp.organization
                                                val studentId = userProfile?.id
                                                val oppTitle = opp.title ?: ""

                                                if (orgId != null && orgId != 0) {
                                                    val notification = NotificationModel(
                                                        receiverId = orgId,
                                                        senderId = studentId ?: 0,
                                                        title = context.getString(R.string.new_join_request),
                                                        message = context.getString(R.string.student_joined_opportunity_msg, oppTitle),
                                                        type = "APPLICATION"
                                                    )
                                                    notViewModel.sendNotification(
                                                        notification,
                                                        {
                                                            Toast.makeText(
                                                                context,
                                                                context.getString(R.string.notification_sent_to_org),
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        },
                                                        {
                                                            Toast.makeText(
                                                                context,
                                                                context.getString(R.string.failed_to_send_notification),
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    context = context
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * تصميم البطاقة الخاصة بكل فرصة تطوعية مع إدارة حالات زر الانضمام.
 */
@Composable
fun OpportunityCard(
    opp: ActivityResponse,
    isThisButtonLoading: Boolean,
    onCardClick: (Int) -> Unit,
    onApplyClick: () -> Unit,
    activityViewModel: ActivityViewModel,
    context: Context
) {
    val myJoinedIds by activityViewModel.myParticipationsIds.collectAsState()

    val hasActiveParticipation = myJoinedIds.isNotEmpty()
    val isAlreadyJoined = opp.id?.let { myJoinedIds.contains(it) } ?: false

    val detailedStatus = opp.status?.trim() ?: ""
    val isActive = detailedStatus.equals("Active", ignoreCase = true) ||
            detailedStatus.equals("Open", ignoreCase = true)

    val canApply = isActive && !isAlreadyJoined && !hasActiveParticipation
    val isButtonEnabled = canApply && !isThisButtonLoading

    val buttonText = when {
        !isActive -> stringResource(R.string.applications_closed)
        isAlreadyJoined -> stringResource(R.string.already_joined)
        hasActiveParticipation -> stringResource(R.string.already_enrolled_another)
        else -> stringResource(R.string.quick_apply)
    }

    val statusColor = if (isActive) Color(0xFF4CAF50) else Color(0xFFE53935)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { opp.id?.let { onCardClick(it) } },
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (opp.title?.firstOrNull()?.toString() ?: "?"),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = opp.title.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.organization_prefix, opp.organization.toString()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = detailedStatus.ifEmpty { stringResource(R.string.closed_status) },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = opp.description.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = opp.category ?: stringResource(R.string.general_category),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Button(
                        onClick = { onApplyClick() },
                        enabled = isButtonEnabled,
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isThisButtonLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = buttonText, fontSize = 12.sp)
                        }
                    }

                    if (!isButtonEnabled && !isThisButtonLoading) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    when {
                                        !isActive -> {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.opportunity_closed_toast),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        isAlreadyJoined -> {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.already_joined_toast),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        hasActiveParticipation -> {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.cannot_join_multiple_toast),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}