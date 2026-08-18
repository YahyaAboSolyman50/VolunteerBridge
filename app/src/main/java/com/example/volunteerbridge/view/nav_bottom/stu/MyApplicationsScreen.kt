package com.example.volunteerbridge.view.nav_bottom.stu

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
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
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.data.model.response.ParticipationResponse
import com.example.volunteerbridge.viewmodelApi.ActivityViewModel
import com.example.volunteerbridge.viewmodelApi.AttendanceViewModel
import com.example.volunteerbridge.viewmodelApi.ParticipationViewModel
import com.valentinilk.shimmer.shimmer

/**
 * شاشة طلبات التطوع الخاصة بالطالب (My Applications)
 * تعرض الطلبات مقسمة حسب الحالة عبر تبويبات (Pending, Approved, Completed, Rejected)
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationsScreen(
    viewModel: ParticipationViewModel,
    attendanceViewModel: AttendanceViewModel,
    token: String,
    activityViewModel: ActivityViewModel
) {
    // مراقبة حالة الطلبات وحالة التحميل من الـ ViewModel
    val applications by viewModel.applications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    // إدارة التبويبات (Tabs) مع جلب الترجمة لكل حالة
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_pending),
        stringResource(R.string.tab_approved),
        stringResource(R.string.tab_completed),
        stringResource(R.string.tab_rejected)
    )

    // جلب طلبات الطالب تلقائياً من الخادم عند فتح الشاشة للمرة الأولى
    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) {
            viewModel.fetchStudentApplications()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.background)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.my_applications),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.background,
                        titleContentColor = colorScheme.onBackground
                    )
                )

                // شريط التبويبات القابل للتمرير (ScrollableTabRow) لتنظيم حالات الطلبات
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = colorScheme.background,
                    contentColor = colorScheme.primary,
                    edgePadding = 16.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            },
                            selectedContentColor = colorScheme.primary,
                            unselectedContentColor = colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        },
        containerColor = colorScheme.background
        // محتوى الشاشة الرئيسي داخل الـ Scaffold
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // عرض هيكل التحميل المتلألئ (Shimmer) بدلاً من مؤشر التحميل الدائري
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(4) {
                        ApplicationCardShimmer()
                    }
                }
            } else {
                // تصفية القائمة بناءً على التبويب المختار حالياً
                val filteredApplications = when (selectedTabIndex) {
                    0 -> applications.filter { it.status.equals("pending", ignoreCase = true) }
                    1 -> applications.filter { it.status.equals("Approved", ignoreCase = true) || it.status.equals("Accepted", ignoreCase = true) }
                    2 -> applications.filter { it.status.equals("completed", ignoreCase = true) || it.status.equals("finished", ignoreCase = true) }
                    3 -> applications.filter { it.status.equals("rejected", ignoreCase = true) }
                    else -> emptyList()
                }

                if (filteredApplications.isEmpty()) {
                    // رسالة واجهة فارغة في حال عدم وجود طلبات في هذا التبويب
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.no_applications_found, tabs[selectedTabIndex]),
                            color = colorScheme.onBackground.copy(alpha = 0.6f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    // عرض الطلبات المصفاة في قائمة عمودية
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredApplications, key = { it.id }) { app ->
                            ApplicationCard(
                                application = app,
                                token = token,
                                attendanceViewModel = attendanceViewModel,
                                activityViewModel = activityViewModel,
                                onCancelClick = {
                                    viewModel.cancelApplication(app.id) { success ->
                                        if (success) {
                                            Toast.makeText(context, context.getString(R.string.application_canceled_success), Toast.LENGTH_SHORT).show()
                                            viewModel.fetchStudentApplications()
                                        } else {
                                            Toast.makeText(context, context.getString(R.string.application_canceled_failure), Toast.LENGTH_SHORT).show()
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
}

/**
 * بطاقة عرض تفاصيل الطلب الفردي (ApplicationCard) بتصميم مخصص لكل حالة
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ApplicationCard(
    application: ParticipationResponse,
    token: String,
    attendanceViewModel: AttendanceViewModel,
    activityViewModel: ActivityViewModel,
    onCancelClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val status = application.status.lowercase()

    // تعريف المتغيرات المنطقية لتحديد حالة الطلب الحالية
    val isPending = status == "pending"
    val isAccepted = status == "accepted" || status == "approved"
    val isCompleted = status == "completed" || status == "finished"
    val isRejected = status == "rejected"

    // تخصيص الألوان لتتطابق مع التصميم الداكن الموحد (مثل الصورة)
    val containerColor = colorScheme.surface // خلفية داكنة موحدة للبطاقة لجميع الحالات

// لون الإطار الخفيف (Border) حسب الحالة
    val borderColor = when {
        isAccepted -> Color(0xFF4CAF50).copy(alpha = 0.4f)  // أخضر هادئ للقبول
        isCompleted -> Color(0xFF2196F3).copy(alpha = 0.4f) // أزرق هادئ للاكتمال
        isRejected -> Color(0xFFF44336).copy(alpha = 0.4f)  // أحمر هادئ للرفض
        else -> colorScheme.outline.copy(alpha = 0.2f)      // افتراضي
    }

// لون شارة الحالة (Badge) والنص الخاص بها
    val badgeColor = when {
        isAccepted -> Color(0xFF4CAF50)
        isCompleted -> Color(0xFF2196F3)
        isRejected -> Color(0xFFF44336)
        else -> Color(0xFFFF9800)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isPending) 4.dp else 1.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // أيقونة دائرية تتغير أيقونتها ولونها حسب حالة الطلب لتوضيح حالته بسرعة
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isAccepted -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                isCompleted -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                isRejected -> colorScheme.error.copy(alpha = 0.2f)
                                else -> colorScheme.primary.copy(alpha = 0.1f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            isAccepted -> Icons.Default.CheckCircle
                            isCompleted -> Icons.Default.Done
                            isRejected -> Icons.Default.Close
                            else -> Icons.Default.Info
                        },
                        contentDescription = null,
                        tint = when {
                            isAccepted -> Color(0xFF4CAF50)
                            isCompleted -> Color(0xFF2196F3)
                            isRejected -> colorScheme.error
                            else -> colorScheme.primary
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // عرض عنوان النشاط وتاريخ التقديم
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = application.activityTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.joined_at_prefix, application.joinedAt.take(10)),
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // شارة حالة الطلب (Status Badge)
                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = application.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                // زر الإلغاء يظهر فقط إذا كان الطلب في حالة Pending
                if (isPending) {
                    TextButton(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF44336)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.cancel_button),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * مكون هيكل التحميل المتلألئ (Shimmer) للبطاقات
 */
@Composable
fun ApplicationCardShimmer() {
    val colorScheme = MaterialTheme.colorScheme

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shimmer(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colorScheme.surfaceVariant)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(colorScheme.surfaceVariant)
                    )
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(colorScheme.surfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorScheme.surfaceVariant)
                )
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorScheme.surfaceVariant)
                )
            }
        }
    }
}