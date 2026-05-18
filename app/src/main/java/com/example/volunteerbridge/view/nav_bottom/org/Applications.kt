package com.example.volunteerbridge.view.nav_bottom.org

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.volunteerbridge.model.classes.ApplicationModel
import com.example.volunteerbridge.view.functions.formatTimeAgo
import com.example.volunteerbridge.model.classes.status.UiState
import com.example.volunteerbridge.viewmodel.OpportunityViewModel
import com.example.volunteerbridge.viewmodel.OrgViewModel

@Composable
fun ApplicationsScreen(oppViewModel: OpportunityViewModel, orgViewModel: OrgViewModel) {
    ApplicationsScreenDesign(oppViewModel, orgViewModel)
}

@Composable
fun ApplicationsScreenDesign(oppViewModel: OpportunityViewModel, orgViewModel: OrgViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pending", "Accepted", "Rejected")

    val applications by orgViewModel.orgApplications
    val orgUiState by orgViewModel.orgUiState.collectAsState()
    val orgModel by orgViewModel.currentOrgData
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(orgModel?.uid) {
        orgModel?.uid?.let { uid ->
            orgViewModel.fetchApplicationsForOrg(uid)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // العنوان الفرعي وحساب الإجمالي
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
                            text = "Total Applications: ${applications.size}",
                            color = colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // التبويبات الفلترة (Tabs)
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = colorScheme.primary,
                    edgePadding = 24.dp,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        val count = applications.count { it.status == title }
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

            // تصفية القائمة محلياً بناءً على التبويب المحدد
            val filteredApps = applications.filter { it.status == tabs[selectedTab] }

            if (filteredApps.isEmpty() && orgUiState !is UiState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No ${tabs[selectedTab]} requests",
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
                            onAccept = { orgViewModel.updateApplicationStatus(app.appId, "Accepted", app.studentId, app.oppTitle) },
                            onReject = { orgViewModel.updateApplicationStatus(app.appId, "Rejected", app.studentId, app.oppTitle) }
                        )
                    }
                }
            }
        }

        // ✨ التعديل الثاني: إظهار مؤشر تحميل تقدمي متناسق إذا كانت البيانات تُجلب بالخلفية لأول مرة
        if (orgUiState is UiState.Loading && applications.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorScheme.primary
            )
        }
    }
}

@Composable
fun ApplicantRequestCard(
    app: ApplicationModel,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = colorScheme.primary)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.studentName,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Applying for: '${app.oppTitle}'",
                        color = colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = formatTimeAgo(app.appliedAt),
                    color = colorScheme.onSurface.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (app.status == "Pending") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onReject,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.errorContainer,
                            contentColor = colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reject")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Accept", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Surface(
                    color = if (app.status == "Accepted") Color(0xFF4CAF50).copy(alpha = 0.1f) else colorScheme.error.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (app.status == "Accepted") "Accepted ✓" else "Rejected ✗",
                        color = if (app.status == "Accepted") Color(0xFF4CAF50) else colorScheme.error,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}