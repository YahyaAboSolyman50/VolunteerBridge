package com.example.volunteerbridge.view.nav_bottom.stu.home

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.model.classes.ApplicationModel
import com.google.firebase.auth.FirebaseAuth
import com.example.volunteerbridge.model.classes.OpportunityModel
import com.example.volunteerbridge.model.classes.status.UiState
import com.example.volunteerbridge.view.functions.formatFullDate
import com.example.volunteerbridge.viewmodel.OpportunityViewModel
import com.example.volunteerbridge.viewmodel.StudentViewModel
import java.util.UUID

@Composable
fun OpportunityDetailScreen(
    oppId: String,
    oppViewModel: OpportunityViewModel,
    stuViewModel: StudentViewModel
) {
    val opportunity = oppViewModel.orgOpp.value.find { it.id == oppId }
        ?: oppViewModel.filteredOppForStudent.value.find { it.id == oppId }

    DetailedDesign(opportunity, oppViewModel, stuViewModel)
}

@Composable
private fun DetailedDesign(
    opp: OpportunityModel?,
    oppViewModel: OpportunityViewModel,
    stuViewModel: StudentViewModel
) {
    val isVerified = opp?.isOrgVerified ?: false
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val context = LocalContext.current

    // 🆔 جلب الـ UID الحالي للطالب
    val currentStudentId = remember { FirebaseAuth.getInstance().currentUser?.uid ?: "" }
    val studentModel by stuViewModel.currentUserData

    // 🔥 مراقبة الـ UiState والـ appliedOppIds من الـ StudentViewModel (مثل الـ Home تماماً)
    val studentUiState by stuViewModel.uiState.collectAsState()
    val appliedOppIds by stuViewModel.appliedOppIds

    val hasAlreadyApplied = appliedOppIds.contains(opp?.id)

    val detailedStatus = if (opp != null) oppViewModel.getDetailedStatus(opp) else "Unknown"

    // تحديد نص الزر بناءً على الفحص الجديد
    val buttonText = when {
        hasAlreadyApplied -> "Applied ✓"
        detailedStatus != "Active" -> "Applications Closed"
        else -> "Quick Apply"
    }

    // تفعيل الزر هندسياً
    val isButtonEnabled = detailedStatus == "Active" && !hasAlreadyApplied && studentUiState !is UiState.Loading

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
            // 1. Header Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = opp?.title ?: "Opportunity Title",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = opp?.orgName ?: "Organization Name",
                            style = MaterialTheme.typography.titleMedium,
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                        if (isVerified) {
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = opp?.orgType ?: "Unknown",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(8.dp))

                        // Badge التنبيه الشكلي لحالة التسجيل المحدثة
                        Surface(
                            color = when {
                                hasAlreadyApplied -> Color(0xFF2196F3).copy(alpha = 0.1f)
                                detailedStatus == "Active" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                                else -> Color.Gray.copy(alpha = 0.1f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (hasAlreadyApplied) "Registered" else detailedStatus,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = when {
                                    hasAlreadyApplied -> Color(0xFF2196F3)
                                    detailedStatus == "Active" -> Color(0xFF4CAF50)
                                    else -> Color.Gray
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. Info Grid
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoCard(Modifier.weight(1f), "Location", opp?.location ?: "N/A", Icons.Default.LocationOn)
                        InfoCard(Modifier.weight(1f), "Hours", "+${opp?.requiredHours ?: 0}h", Icons.Default.Star)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val seatsLeft = (opp?.vacancies ?: 0) - (opp?.applicantsCount ?: 0)
                        InfoCard(Modifier.weight(1f), "Seats Left", "$seatsLeft/${opp?.vacancies}", Icons.Default.AccountCircle)
                        InfoCard(Modifier.weight(1f), "Deadline",
                            formatFullDate(opp?.deadline ?: 0), Icons.Default.DateRange)
                    }
                }
            }

            // 3. Description Section
            item {
                SectionTitle("Description")
                Text(
                    text = opp?.description ?: "No description provided.",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    lineHeight = 22.sp
                )
            }

            // 4. Requirements Section
            if (!opp?.requirements.isNullOrEmpty()) {
                item {
                    SectionTitle("Requirements")
                    opp?.requirements?.forEach { req ->
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", fontWeight = FontWeight.Bold, color = primaryColor)
                            Spacer(Modifier.width(8.dp))
                            Text(text = req, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        // 5. Fixed Bottom Apply Button
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shadowElevation = 20.dp
        ) {
            Button(
                onClick = {
                    if (opp != null && studentModel != null) {
                        stuViewModel.appRegister(
                            ApplicationModel(
                                UUID.randomUUID().toString(),
                                opp.id,
                                studentModel?.uid ?: "",
                                opp.orgId,
                                opp.title,
                                studentModel?.name ?: "",
                                opp.orgName,
                            )
                        )
                    } else {
                        Toast.makeText(context, "User profile loading, please wait...", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasAlreadyApplied) Color.Gray else primaryColor,
                    disabledContainerColor = if (hasAlreadyApplied) Color.Gray.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                if (studentUiState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = if (hasAlreadyApplied) Color.White else Color.Black
                    )
                }
            }
        }
    }
}

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

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}