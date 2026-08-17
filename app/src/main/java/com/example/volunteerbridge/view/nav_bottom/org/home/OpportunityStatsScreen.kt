package com.example.volunteerbridge.view.nav_bottom.org.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.data.model.response.ActivityResponse
import com.example.volunteerbridge.data.model.response.ParticipationResponse
import com.example.volunteerbridge.viewmodelApi.ActivityViewModel
import com.example.volunteerbridge.viewmodelApi.OrganizationViewModel
import com.valentinilk.shimmer.shimmer
import java.time.LocalDate

/**
 * شاشة إحصائيات الفرصة للمؤسسة عبر جلب البيانات من الـ API مباشرة باستخدام ID مع دعم الشيمر الهيكلي
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OpportunityStatsScreen(
    oppId: String,
    orgViewModel: OrganizationViewModel,
    oppViewModel: ActivityViewModel,
    onAllAppClick: (Int) -> Unit,
    onManageClick: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current

    var currentActivity by remember { mutableStateOf<ActivityResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // جلب تفاصيل الفرصة بناءً على الـ ID عند فتح الشاشة
    LaunchedEffect(oppId) {
        val idInt = oppId.toIntOrNull()
        if (idInt != null) {
            oppViewModel.getActivityById(idInt) { activity ->
                currentActivity = activity
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    val oppList by oppViewModel.myActivities.collectAsState()
    val isTopPerforming = remember(oppList, currentActivity) {
        val maxApplicants = oppList.maxOfOrNull { it.applicantsCount ?: 0 } ?: 0
        val currentApplicants = currentActivity?.applicantsCount ?: 0
        currentActivity != null && currentApplicants == maxApplicants && maxApplicants > 0
    }

    val allApplications by orgViewModel.orgApplications
    val currentOppApplications = remember(allApplications, oppId) {
        allApplications.filter { it.id.toString() == oppId }
    }

    // التحقق من حالة النشاط: بناءً على الحالة النصية/الرقمية وأيضاً مقارنة تاريخ النهاية مع التاريخ الحالي
    val isActive = remember(currentActivity) {
        val activity = currentActivity
        if (activity == null) {
            false
        } else {
            val statusCondition = activity.status.equals("Active", ignoreCase = true) || activity.status == "1"

            // التحقق من أن تاريخ اليوم لم يتجاوز تاريخ انتهاء الفرصة (EndDate)
            val isNotExpired = try {
                if (!activity.endDate.isNullOrBlank()) {
                    val parsedDate = LocalDate.parse(activity.endDate.take(10))
                    !LocalDate.now().isAfter(parsedDate)
                } else {
                    true
                }
            } catch (e: Exception) {
                true
            }

            statusCondition && isNotExpired
        }
    }

    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        floatingActionButton = {
            if (!isLoading) {
                ExtendedFloatingActionButton(
                    onClick = { onManageClick(currentActivity?.id ?: oppId.toIntOrNull() ?: 0) },
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    text = { Text(stringResource(R.string.manage_opportunity)) }
                )
            }
        }
    ) { padding ->
        if (isLoading) {
            // استبدال مؤشر التحميلي الدائري بتصميم الشيمر الهيكلي المتناسق
            OpportunityStatsShimmer(padding = padding)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                HeaderSectionStats(
                    title = currentActivity?.title,
                    isActive = isActive,
                    onBackClick = onBackClick,
                    icon = {
                        if (isTopPerforming) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )

                Spacer(Modifier.height(30.dp))

                PerformanceSection(
                    applicantsCount = currentActivity?.applicantsCount ?: 0,
                    vacancies = currentActivity?.volunteerLimit ?: 0
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatSmallCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.category),
                        info = currentActivity?.category ?: stringResource(R.string.general_category)
                    )

                    RecentApplicantsCard(
                        modifier = Modifier.weight(1.2f),
                        applicants = currentOppApplications.take(3)
                    )
                }

                Spacer(Modifier.height(16.dp))

                TimelineSection(
                    startDate = currentActivity?.startDate ?: stringResource(R.string.not_set),
                    endDate = currentActivity?.endDate ?: stringResource(R.string.not_set)
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        onAllAppClick(currentActivity?.id ?: oppId.toIntOrNull() ?: 0)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.view_all_applicants_count, currentOppApplications.size),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OpportunityStatsShimmer(padding: PaddingValues) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(20.dp)
            .shimmer(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // رأس الشاشة الوهمي
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colorScheme.surfaceVariant, CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(18.dp)
                        .background(colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(14.dp)
                        .background(colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // بطاقات الأداء الوهمية (PerformanceSection)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
            )
        }

        // بطاقات القسم والمتقدمين الوهمية
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            )
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .height(110.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            )
        }

        // بطاقات التواريخ الوهمية (TimelineSection)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            )
        }

        // زر العرض الوهمي بالأفل
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
        )
    }
}

@Composable
fun HeaderSectionStats(
    title: String?, isActive: Boolean? = false, onBackClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Log.d("ActivityStatus", "Status is active: ${isActive}")
    val colorScheme = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = colorScheme.onBackground)
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(colorScheme.primary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = title ?: stringResource(R.string.loading_text),
                color = colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = if (isActive == true) Color(0xFF4CAF50).copy(alpha = 0.15f) else colorScheme.error.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = if (isActive == true) stringResource(R.string.status_active) else stringResource(R.string.status_closed),
                    color = if (isActive == true) Color(0xFF4CAF50) else colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier,
    label: String,
    value: String,
    accentColor: Color
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                color = colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = if (accentColor == Color.White) colorScheme.onSurface else accentColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun RecentApplicantsCard(modifier: Modifier, applicants: List<ParticipationResponse>) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.recent_title), color = colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))

            if (applicants.isEmpty()) {
                Text(stringResource(R.string.no_applicants_yet), color = colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 11.sp)
            } else {
                applicants.forEach { app ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = app.user.toString().take(1).uppercase(),
                                color = colorScheme.primary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = app.user.toString(),
                            color = colorScheme.onSurface,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatSmallCard(modifier: Modifier, title: String, info: String) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                text = info,
                color = colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DateCard(
    modifier: Modifier,
    label: String,
    date: String,
    iconColor: Color
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(iconColor, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier.height(10.dp))
            Text(
                text = date,
                color = colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TimelineSection(startDate: String?, endDate: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DateCard(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.start_date_label),
            date = startDate ?: stringResource(R.string.not_set),
            iconColor = Color(0xFF4DB6AC)
        )

        DateCard(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.end_date_label),
            date = endDate ?: stringResource(R.string.not_set),
            iconColor = Color(0xFFFF8A65)
        )
    }
}

@Composable
fun PerformanceSection(applicantsCount: Long, vacancies: Long) {
    val remaining = (vacancies - applicantsCount).coerceAtLeast(0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.reserved_slots_label),
            value = "$applicantsCount",
            accentColor = Color.White
        )

        StatCard(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.remaining_slots_label),
            value = "$remaining",
            accentColor = if (remaining > 0) Color(0xFF4DB6AC) else Color(0xFFFF4B4B)
        )
    }
}