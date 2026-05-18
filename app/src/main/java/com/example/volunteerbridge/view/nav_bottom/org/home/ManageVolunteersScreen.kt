package com.example.volunteerbridge.view.nav_bottom.org.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.model.classes.ApplicationModel
import com.example.volunteerbridge.model.classes.OpportunityModel
import com.example.volunteerbridge.viewmodel.OrgViewModel

@Composable
fun ManageVolunteersScreen(
    opportunity: OpportunityModel,
    orgViewModel: OrgViewModel,
    onBackClick: () -> Unit
) {
    val allApplications by orgViewModel.orgApplications

    // فلترة الطلاب المقبولين فقط (Accepted) في هذه الفرصة
    val activeVolunteers = remember(allApplications, opportunity.id) {
        allApplications.filter { it.oppId == opportunity.id && it.status == "Accepted" }
    }

    // حالات التحكم في حوار إضافة مهمة
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedStudentId by remember { mutableStateOf("") }
    var selectedStudentName by remember { mutableStateOf("") }

    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = "Manage Volunteers",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
            }
        },
        containerColor = colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (activeVolunteers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active volunteers in this opportunity yet.",
                        color = colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeVolunteers) { volunteer ->
                        VolunteerCard(
                            volunteer = volunteer,
                            requiredHours = opportunity.requiredHours,
                            onAssignTaskClick = {
                                // تجهيز بيانات الطالب المحدّد وفتح الحوار
                                selectedStudentId = volunteer.studentId
                                selectedStudentName = volunteer.studentName
                                showAddTaskDialog = true
                            },
                            onFinalizeClick = {
                                orgViewModel.finalizeAndCreditStudent(
                                    appId = volunteer.appId,
                                    studentId = volunteer.studentId,
                                    requiredHours = opportunity.requiredHours,
                                    oppTitle = opportunity.title
                                )
                            }
                        )
                    }
                }
            }

            // ✨ استدعاء الحوار وحفظ المهمة في قاعدة البيانات حياً
            if (showAddTaskDialog) {
                AddTaskDialog(
                    generalDuties = opportunity.tasks, // نمرر مصفوفة الشروحات العامة للاقتراح
                    onDismiss = { showAddTaskDialog = false },
                    onConfirm = { title, description, dueDate ->
                        orgViewModel.assignTaskToStudent(
                            oppId = opportunity.id,
                            oppTitle = opportunity.title,
                            studentId = selectedStudentId,
                            title = title,
                            description = description,
                            dueDate = dueDate
                        )
                        showAddTaskDialog = false // إغلاق الحوار بعد النجاح
                    }
                )
            }
        }
    }
}

@Composable
fun VolunteerCard(
    volunteer: ApplicationModel,
    requiredHours: Int,
    onAssignTaskClick: () -> Unit,
    onFinalizeClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

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
                            text = volunteer.studentName.take(1).uppercase(),
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = volunteer.studentName,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = "Status: Working",
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
                        text = "$requiredHours Hrs Reward",
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
                OutlinedButton(
                    onClick = onAssignTaskClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Assign Task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onFinalizeClick,
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Finalize & Credit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    generalDuties: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, dueDate: Long) -> Unit
) {
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }

    val defaultDueDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign New Task", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                if (generalDuties.isNotEmpty()) {
                    Text("Suggestions from Opportunity:", style = MaterialTheme.typography.bodySmall)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        generalDuties.take(2).forEach { duty ->
                            SuggestionChip(
                                onClick = { taskTitle = duty },
                                label = { Text(duty, maxLines = 1) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Task Description / Instructions") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        onConfirm(taskTitle, taskDescription, defaultDueDate)
                    }
                }
            ) {
                Text("Assign")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}