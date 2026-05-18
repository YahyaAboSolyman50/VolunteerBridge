package com.example.volunteerbridge.view.nav_bottom.org.opps

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.model.classes.OpportunityModel
import com.example.volunteerbridge.model.classes.status.UiState
import com.example.volunteerbridge.viewmodel.OpportunityViewModel
import com.example.volunteerbridge.viewmodel.OrgViewModel

@Composable
fun ManageScreen(
    orgViewModel: OrgViewModel,
    oppViewModel: OpportunityViewModel,
    onEditClick: (String) -> Unit,
    onViewDetailOppClick: (String) -> Unit,
    onCreateOppClick: () -> Unit // ✨ دالة الانتقال لصفحة الإضافة الجديدة
) {
    ManageScreenDesign(orgViewModel, oppViewModel, onEditClick, onViewDetailOppClick, onCreateOppClick)
}

@Composable
fun ManageScreenDesign(
    orgViewModel: OrgViewModel,
    oppViewModel: OpportunityViewModel,
    onEditClick: (String) -> Unit,
    onViewDetailOppClick: (String) -> Unit,
    onCreateOppClick: () -> Unit
) {
    val uiState by oppViewModel.uiState.collectAsState()
    val oppList by oppViewModel.orgOpp
    val orgData by orgViewModel.currentOrgData

    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val isLoading = uiState is UiState.Loading

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
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // --- Custom Top Bar (Header) ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.manageOpp),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = primaryColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.2f))
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            text = "${oppList.size} ${stringResource(R.string.ActivePost)}",
                            color = primaryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- Content Section ---
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                if (isLoading && oppList.isEmpty()) {
                    LoadingShimmerList()
                } else if (oppList.isEmpty()) {
                    EmptyState()
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        oppList.forEach { opportunity ->
                            OrgManageCard(
                                opportunity,
                                oppViewModel,
                                onEditClick = { onEditClick(opportunity.id) },
                                onViewDetailOppClick = { onViewDetailOppClick(opportunity.id) },
                                accentColor = primaryColor
                            )
                        }
                    }
                }
            }
        }
    }

    // --- Floating Action Button يوجه لصفحة مستقلة الآن ---
    Box(modifier = Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            text = { Text("New Post", color = Color.Black, fontWeight = FontWeight.Bold) },
            icon = { Icon(Icons.Filled.Add, null, tint = Color.Black) },
            onClick = {
                oppViewModel.resetState()
                onCreateOppClick() // ✨ الانتقال المباشر
            },
            containerColor = primaryColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }
}

@Composable
fun OrgManageCard(
    opportunityModel: OpportunityModel,
    oppViewModel: OpportunityViewModel,
    onEditClick: () -> Unit,
    onViewDetailOppClick: () -> Unit,
    accentColor: Color
) {
    val displayStatus = oppViewModel.getDetailedStatus(opportunityModel)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = opportunityModel.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = if (displayStatus == "Active") Color(0xFF4CAF50).copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = displayStatus,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (displayStatus == "Active") Color(0xFF4CAF50) else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${opportunityModel.applicantsCount} Applicants",
                        style = MaterialTheme.typography.labelMedium,
                        color = accentColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Text("Edit Details", fontSize = 13.sp)
                }

                Button(
                    onClick = onViewDetailOppClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("View", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun LoadingShimmerList() {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(3) { ShimmerCardItem() }
    }
}

@Composable
fun ShimmerCardItem() {
    val transition = rememberInfiniteTransition(label = "")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(50.dp).background(brush, CircleShape))
        Spacer(Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.fillMaxWidth(0.6f).height(15.dp).background(brush))
            Box(modifier = Modifier.fillMaxWidth(0.4f).height(10.dp).background(brush))
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Opportunities Yet",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF042A63),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the '+' button to post your first volunteer opportunity.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}