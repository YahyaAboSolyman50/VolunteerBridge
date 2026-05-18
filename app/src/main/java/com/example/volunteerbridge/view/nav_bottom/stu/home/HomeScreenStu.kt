package com.example.volunteerbridge.view.nav_bottom.stu.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.volunteerbridge.model.classes.ApplicationModel
import com.example.volunteerbridge.model.classes.OpportunityConstants
import com.example.volunteerbridge.model.classes.OpportunityModel
import com.example.volunteerbridge.model.classes.Organization
import com.example.volunteerbridge.model.classes.SubClasses
import com.example.volunteerbridge.model.classes.UserModel
import com.example.volunteerbridge.model.classes.status.UiState
import com.example.volunteerbridge.viewmodel.OpportunityViewModel
import com.example.volunteerbridge.viewmodel.OrgViewModel
import com.example.volunteerbridge.viewmodel.StudentViewModel
import java.util.UUID

@Composable
fun StudentHomeContent(
    orgViewModel: OrgViewModel,
    stuViewModel: StudentViewModel,
    oppViewModel: OpportunityViewModel,
    navController: NavController
) {
    StudentHomeDesign(orgViewModel, stuViewModel, oppViewModel, navController)
}

@Composable
fun StudentHomeDesign(
    orgViewModel: OrgViewModel,
    stuViewModel: StudentViewModel,
    oppViewModel: OpportunityViewModel,
    navController: NavController
) {
    val userModel by stuViewModel.currentUserData
    val oppModel by oppViewModel.filteredOppForStudent
    val orgModel by orgViewModel.currentOrgData

    var searchQuery by remember { mutableStateOf("") }
    val targetHours = 50f

    val currentHours = userModel?.totalHours?.toFloat() ?: 0f
    val progressValue = (currentHours / targetHours).coerceIn(0f, 1.0f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Welcome, ${userModel?.name ?: "User"}!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 17.sp
                    )
                    Text(
                        "Ready to make an impact today?",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .clickable { /* تفعيل الانتقال للبروفايل لاحقاً */ }
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                )
            }
        }

        // Statistics Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Volunteer Hours", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "$currentHours Hours",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = progressValue,
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

        // ✨ تعديل 2: ربط حقل البحث الفعلي بالـ ViewModel لإجراء الفلترة أثناء الكتابة
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    oppViewModel.filterStudentData(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search for an opportunity...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // List Title & Advanced Filters
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Available Opportunities",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    item {
                        FilterDropdown(
                            label = "Status",
                            options = OpportunityConstants.statuses,
                            selectedOption = oppViewModel.selectedStatus
                        ) {
                            oppViewModel.selectedStatus = it
                            oppViewModel.filterStudentData(searchQuery)
                        }
                    }
                    item {
                        FilterDropdown(
                            label = "Type",
                            options = OpportunityConstants.orgTypes,
                            selectedOption = oppViewModel.selectedOrgType
                        ) {
                            oppViewModel.selectedOrgType = it
                            oppViewModel.filterStudentData(searchQuery)
                        }
                    }
                    item {
                        FilterDropdown(
                            label = "Category",
                            options = OpportunityConstants.categories,
                            selectedOption = oppViewModel.selectedCategory
                        ) {
                            oppViewModel.selectedCategory = it
                            oppViewModel.filterStudentData(searchQuery)
                        }
                    }
                }
            }
        }

        // Opportunities List
        items(oppModel.size) { index ->
            val opp = oppModel[index]
            OpportunityCard(
                opp = opp,
                org = orgModel,
                stu = userModel,
                oppViewModel = oppViewModel,
                stuViewModel = stuViewModel, // ✨ مررنا الـ stuViewModel هنا
                onClick = { id ->
                    navController.navigate("${SubClasses.SubClassesStu.OppDetail.route}/$id")
                }
            )
        }
    }
}


@Composable
fun OpportunityCard(
    opp: OpportunityModel,
    org: Organization?,
    stu: UserModel?,
    oppViewModel: OpportunityViewModel,
    stuViewModel: StudentViewModel, // ✨ استلام الـ stuViewModel الجديد
    onClick: (String) -> Unit
) {
    // ✨ تعديل 1: مراقبة الـ UiState الخاص بالطالب لإدارة عمليات الـ Loading عند الضغط على التقديم
    val studentUiState by stuViewModel.uiState.collectAsState()
    val detailedStatus = oppViewModel.getDetailedStatus(opp)
    val statusColor = if (detailedStatus == "Active") Color(0xFF4CAF50) else Color(0xFFE53935)

    var isThisButtonLoading by remember { mutableStateOf(false) }

    LaunchedEffect(studentUiState) {
        if (studentUiState !is UiState.Loading) {
            isThisButtonLoading = false
        }
    }

    // ✨ تعديل 1: قراءة الـ appliedOppIds من الـ StudentViewModel بعد النقل
    val appliedOppIds by stuViewModel.appliedOppIds

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick(opp.id) },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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
                            text = opp.orgName.firstOrNull()?.toString() ?: "?",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = opp.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = opp.orgName,
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
                        text = detailedStatus,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = opp.description,
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = opp.category,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = opp.orgType,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = {
                        isThisButtonLoading = true
                        // ✨ تعديل 1: استدعاء دالة التقديم من الـ stuViewModel الجديد مباشرة
                        stuViewModel.appRegister(
                            ApplicationModel(
                                UUID.randomUUID().toString(),
                                opp.id,
                                stu?.uid ?: "",
                                opp.orgId,
                                opp.title,
                                stu?.name ?: "",
                                opp.orgName,
                            )
                        )
                    },
                    enabled = detailedStatus == "Active" && !isThisButtonLoading && !appliedOppIds.contains(opp.id),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (isThisButtonLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        if (appliedOppIds.contains(opp.id))
                            Text(text = "Applied ✓", fontSize = 14.sp)
                        else
                            Text("Quick Apply", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(4.dp)) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .background(MaterialTheme.colorScheme.surfaceVariant),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$label: $selectedOption", style = MaterialTheme.typography.bodySmall)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}