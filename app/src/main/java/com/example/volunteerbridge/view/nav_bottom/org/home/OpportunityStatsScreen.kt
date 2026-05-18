package com.example.volunteerbridge.view.nav_bottom.org.home

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.model.classes.ApplicationModel
import com.example.volunteerbridge.view.functions.formatFullDate
import com.example.volunteerbridge.viewmodel.OpportunityViewModel
import com.example.volunteerbridge.viewmodel.OrgViewModel

/**
 * شاشة إحصائيات الفرصة للمؤسسة
 * تعرض: عدد المتقدمين، الشواغر المتبقية، التصنيف، وقائمة مصغرة لأحدث المتقدمين.
 */
@Composable
fun OpportunityStatsScreen(
    oppId: String,
    orgViewModel: OrgViewModel,          // ✨ تعديل: إصلاح تعريف الباراميتر الأول
    oppViewModel: OpportunityViewModel,  // ✨ تعديل: إصلاح تعريف الباراميتر الثاني
    onBackClick: () -> Unit,
    onAllAppClick: (String) -> Unit
) {
    val oppList by oppViewModel.orgOpp
    val opp = remember(oppList, oppId) { oppList.find { it.id == oppId } }

    val isTopPerforming = remember(oppList, opp) {
        val maxApplicants = oppList.maxOfOrNull { it.applicantsCount } ?: 0
        opp != null && opp.applicantsCount == maxApplicants && maxApplicants > 0
    }

    // ✨ تعديل: إصلاح الخطأ الإملائي وقراءة البيانات بشكل صحيح من الـ ViewModel الممرر
    val allApplications by orgViewModel.orgApplications
    val currentOppApplications = remember(allApplications, oppId) {
        allApplications.filter { it.oppId == oppId }
    }

    val orgModel by orgViewModel.currentOrgData

    // ✨ إضافة: التأكد من جلب تحديثات الطلبات فور فتح الشاشة لضمان عدم ظهور القائمة فارغة
    LaunchedEffect(orgModel?.uid) {
        orgModel?.uid?.let { uid ->
            orgViewModel.fetchApplicationsForOrg(uid)
        }
    }

    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* أضف منطق التعديل هنا */ },
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                text = { Text("Manage Opportunity") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            HeaderSectionStats(
                title = opp?.title,
                isActive = opp?.status == "Active",
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

            // 2. كارد الأداء الرئيسي
            PerformanceSection(
                applicantsCount = opp?.applicantsCount ?: 0,
                vacancies = opp?.vacancies ?: 0
            )

            Spacer(Modifier.height(20.dp))

            // 3. صف التصنيف وأحدث المتقدمين
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatSmallCard(
                    modifier = Modifier.weight(1f),
                    title = "Category",
                    info = opp?.category ?: "General"
                )

                RecentApplicantsCard(
                    modifier = Modifier.weight(1.2f),
                    applicants = currentOppApplications.take(3)
                )
            }

            Spacer(Modifier.height(16.dp))

            TimelineSection(
                startDate = formatFullDate(opp?.startDate ?: 0),
                endDate = formatFullDate(opp?.endDate ?: 0)
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onAllAppClick(opp?.id ?: "") },
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
                    text = "View All ${currentOppApplications.size} Applicants",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun HeaderSectionStats(
    title: String?, isActive: Boolean? = false, onBackClick: () -> Unit,
    icon: @Composable () -> Unit
) {
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
                text = title ?: "Loading...",
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
                    text = if (isActive == true) "● Active" else "● Closed",
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
fun RecentApplicantsCard(modifier: Modifier, applicants: List<ApplicationModel>) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Recent", color = colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))

            if (applicants.isEmpty()) {
                Text("No applicants yet", color = colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 11.sp)
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
                                text = app.studentName.take(1).uppercase(),
                                color = colorScheme.primary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = app.studentName,
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
            Spacer(modifier = Modifier.height(10.dp))
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
            label = "Start Date",
            date = startDate ?: "Not set",
            iconColor = Color(0xFF4DB6AC)
        )

        DateCard(
            modifier = Modifier.weight(1f),
            label = "End Date",
            date = endDate ?: "Not set",
            iconColor = Color(0xFFFF8A65)
        )
    }
}

@Composable
fun PerformanceSection(applicantsCount: Int, vacancies: Int) {
    val remaining = (vacancies - applicantsCount).coerceAtLeast(0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Reserved Slots",
            value = "$applicantsCount",
            accentColor = Color.White
        )

        StatCard(
            modifier = Modifier.weight(1f),
            label = "Remaining Slots",
            value = "$remaining",
            accentColor = if (remaining > 0) Color(0xFF4DB6AC) else Color(0xFFFF4B4B)
        )
    }
}