package com.example.volunteerbridge.view.admin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.data.model.response.OrganizationResponse
import com.example.volunteerbridge.model.classes.status.UiState
import com.example.volunteerbridge.viewmodelApi.AdminViewModelApi
import com.example.volunteerbridge.viewmodelApi.AuthViewModelApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    authViewModelApi: AuthViewModelApi,
    adminViewModelApi: AdminViewModelApi,
    onLogoutSuccess: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    val pendingOrgs by adminViewModelApi.pendingOrganizations
    val adminUiState by adminViewModelApi.adminUiState.collectAsState()

    val isOperationLoading = adminUiState is UiState.Loading
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_pending_orgs),
        stringResource(R.string.tab_activities_management),
        stringResource(R.string.tab_reports_stats)
    )

    LaunchedEffect(Unit) {
        adminViewModelApi.fetchPendingOrganizations()
    }

    LaunchedEffect(adminUiState) {
        when (val state = adminUiState) {
            is UiState.Success -> {
                Toast.makeText(context, state.data, Toast.LENGTH_SHORT).show()
                adminViewModelApi.resetUiState()
            }
            is UiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                adminViewModelApi.resetUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.admin_dashboard_title),
                        fontWeight = FontWeight.ExtraBold,
                        color = colorScheme.onPrimary,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            authViewModelApi.logout()
                            onLogoutSuccess()
                        },
                        enabled = !isOperationLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = stringResource(R.string.admin_logout_content_desc),
                            tint = colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorScheme.background)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = colorScheme.surface,
                contentColor = colorScheme.primary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = colorScheme.primary,
                            height = 3.dp
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                when (selectedTab) {
                    0 -> PendingOrganizationsTab(
                        pendingOrgs = pendingOrgs,
                        isOperationLoading = isOperationLoading,
                        adminViewModelApi = adminViewModelApi
                    )
                    1 -> AdminActivitiesManagementTab(adminViewModelApi = adminViewModelApi)
                    2 -> AdminReportsAndStatsTab(adminViewModelApi = adminViewModelApi)
                }
            }
        }
    }
}

@Composable
fun PendingOrganizationsTab(
    pendingOrgs: List<OrganizationResponse>,
    isOperationLoading: Boolean,
    adminViewModelApi: AdminViewModelApi
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.pending_orgs_count, pendingOrgs.size),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (pendingOrgs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_pending_orgs),
                    color = colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(pendingOrgs) { org ->
                    AdminOrgRequestCard(
                        org = org,
                        isGlobalLoading = isOperationLoading,
                        onApprove = { adminViewModelApi.approveOrganization(org.id) },
                        onReject = { adminViewModelApi.rejectOrganization(org.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminOrgRequestCard(
    org: OrganizationResponse,
    isGlobalLoading: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var isApproving by remember { mutableStateOf(false) }
    var isRejecting by remember { mutableStateOf(false) }

    val defaultOrgName = stringResource(R.string.default_org_name)
    val licenseLabel = stringResource(R.string.license_number_label)
    val phoneLabel = stringResource(R.string.contact_phone_label)
    val noDescText = stringResource(R.string.no_org_description)

    LaunchedEffect(isGlobalLoading) {
        if (!isGlobalLoading) {
            isApproving = false
            isRejecting = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = org.name.ifBlank { defaultOrgName },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                org.category?.let { type ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = type,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            org.license?.let { if (it.isNotBlank()) InfoRowSimple(Icons.Default.Lock, licenseLabel, it) }
            org.phone?.let { if (it.isNotBlank()) InfoRowSimple(Icons.Default.Phone, phoneLabel, it) }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (!org.description.isNullOrBlank()) org.description else noDescText,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 0.5.dp, color = colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { isApproving = true; onApprove() },
                    modifier = Modifier.weight(1f).height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isGlobalLoading
                ) {
                    if (isApproving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.Done, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.btn_approve), fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = { isRejecting = true; onReject() },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colorScheme.error),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.error),
                    enabled = !isGlobalLoading
                ) {
                    if (isRejecting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = colorScheme.error, strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.btn_reject), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminActivitiesManagementTab(adminViewModelApi: AdminViewModelApi) {
    val activities by adminViewModelApi.activities
    val colorScheme = MaterialTheme.colorScheme

    val defaultTitle = stringResource(R.string.default_activity_title)
    val defaultDesc = stringResource(R.string.default_activity_description)

    LaunchedEffect(Unit) {
        adminViewModelApi.fetchActivities()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.activities_management_count, activities.size),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (activities.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_activities_registered),
                    color = colorScheme.onSurface.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(activities) { activity ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                        border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = activity.title ?: defaultTitle,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activity.description ?: defaultDesc,
                                color = colorScheme.onSurface.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminReportsAndStatsTab(adminViewModelApi: AdminViewModelApi) {
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        adminViewModelApi.fetchReports()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = colorScheme.primary.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.reports_stats_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.reports_stats_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InfoRowSimple(icon: ImageVector, label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label: ", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurface.copy(alpha = 0.5f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
    }
}