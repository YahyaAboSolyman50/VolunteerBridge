package com.example.volunteerbridge.view.nav_bottom.org.home

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
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
import com.example.volunteerbridge.viewmodelApi.AttendanceViewModel
import com.example.volunteerbridge.viewmodelApi.OrganizationViewModel
import com.example.volunteerbridge.viewmodelApi.ParticipationViewModel
import com.valentinilk.shimmer.shimmer

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ManageVolunteersScreen(
    token: String,
    activityResponse: ActivityResponse,
    orgViewModel: OrganizationViewModel,
    participationViewModel: ParticipationViewModel,
    attendanceViewModel: AttendanceViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val applications by participationViewModel.applications.collectAsState()
    val isLoading by participationViewModel.isLoading.collectAsState()
    val attendanceReport by attendanceViewModel.attendanceReport.collectAsState()

    // ضمان الحصول على قيمة ساعات صحيحة
    val validHours = activityResponse.hours ?: 0

    LaunchedEffect(activityResponse.id) {
        android.util.Log.d("ActivityHours", "Current hours: ${activityResponse.hours}")
        participationViewModel.fetchApplicantsForActivity(activityResponse.id ?: 0)
    }

    val activeVolunteers = remember(applications) {
        applications.filter { it.status?.trim()?.equals("Approved", ignoreCase = true) == true }
    }

    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = stringResource(R.string.manage_volunteers_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
            }
        },
        containerColor = colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                ManageVolunteersShimmer()
            } else if (activeVolunteers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.no_active_volunteers_yet), color = colorScheme.onSurface.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Button(
                            onClick = {
                                if (validHours <= 0) {
                                    Toast.makeText(context, "الرجاء التأكد من أن ساعات الفرصة أكبر من صفر", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                activeVolunteers.forEach { volunteer ->
                                    participationViewModel.completeParticipation(participationId = volunteer.id ?: 0, hours = activityResponse.hours ?: 0) { success ->
                                        if (success) {
                                            participationViewModel.fetchApplicantsForActivity(activityResponse.id ?: 0)
                                        }
                                    }
                                }
                                Toast.makeText(context, context.getString(R.string.done_added_toast), Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Done, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.finalize_all_for_everyone))
                        }
                    }

                    item {
                        attendanceReport?.let { report ->
                            // عرض التقرير
                        }
                    }

                    items(activeVolunteers) { volunteer ->
                        VolunteerCard(
                            volunteer = volunteer,
                            requiredHours = validHours, // استخدام القيمة الموثوقة
                            onFinalizeClick = {
                                if (validHours <= 0) {
                                    Toast.makeText(context, "لا يمكن إتمام الفرصة بساعات صفر", Toast.LENGTH_SHORT).show()
                                } else {
                                    participationViewModel.completeParticipation(participationId = volunteer.id ?: 0 , activityResponse.hours?: 0) { success ->
                                        if (success) {
                                            Toast.makeText(context, context.getString(R.string.done_added_toast), Toast.LENGTH_SHORT).show()
                                            participationViewModel.fetchApplicantsForActivity(activityResponse.id ?: 0)
                                        }
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

// بقية الدوال (ManageVolunteersShimmer و VolunteerCard) تبقى كما هي مع التأكد من تمرير validHours لها.

@Composable
fun ManageVolunteersShimmer() {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .shimmer(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        repeat(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            )
        }
    }
}

@Composable
fun VolunteerCard(
    volunteer: com.example.volunteerbridge.data.model.response.ParticipationResponse,
    requiredHours: Int,
    onFinalizeClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val workingStatusText = stringResource(R.string.status_working)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (volunteer.id ?: 0).toString().take(1).uppercase(),
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.volunteer_name_format, volunteer.studentName ?: "#${volunteer.id}"),
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.status_format, volunteer.status ?: workingStatusText),
                            fontSize = 12.sp,
                            color = Color(0xFF2196F3)
                        )
                    }
                }

                Surface(
                    color = colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.hrs_reward_format, requiredHours),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onFinalizeClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.finalize_and_credit), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}