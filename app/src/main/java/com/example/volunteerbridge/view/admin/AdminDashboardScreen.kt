package com.example.volunteerbridge.view.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.data.model.response.OrganizationResponse
import com.example.volunteerbridge.model.classes.status.UiState
import com.example.volunteerbridge.viewmodelApi.AuthViewModelApi
import com.example.volunteerbridge.viewmodelApi.AdminViewModelApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    authViewModelApi: AuthViewModelApi,
    adminViewModelApi: AdminViewModelApi,
    onLogoutSuccess: () -> Unit
) {
    val context = LocalContext.current

    // 🔍 مراقبة البيانات والـ UiState من AdminViewModelApi
    val pendingOrgs by adminViewModelApi.pendingOrganizations
    val adminUiState by adminViewModelApi.adminUiState.collectAsState()

    // تحديد حالة التحميل لتجميد الأزرار لمنع التكرار
    val isOperationLoading = adminUiState is UiState.Loading

    // 🔄 جلب البيانات فور بناء الشاشة
    LaunchedEffect(Unit) {
        adminViewModelApi.fetchPendingOrganizations()
    }

    // ⚡ مراقبة التنبيهات من الـ API (توفير التوست وإعادة الضبط)
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
        topBar = {
            TopAppBar(
                title = { Text("لوحة تحكم إدارة الجامعة", fontWeight = FontWeight.Bold, color = Color.White) },
                actions = {
                    IconButton(
                        onClick = {
//                            authViewModelApi.logout()
                            onLogoutSuccess()
                        },
                        enabled = !isOperationLoading
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "خروج", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF042A63))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
                .padding(16.dp)
        ) {
            Text(
                text = "طلبات التوثيق المعلقة (${pendingOrgs.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF042A63),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (pendingOrgs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "لا توجد طلبات توثيق معلقة حالياً ومرفوعة من المؤسسات. 👍",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pendingOrgs) { org ->
                        AdminOrgRequestCard(
                            org = org,
                            isGlobalLoading = isOperationLoading,
                            onApprove = {
                                adminViewModelApi.approveOrganization(org.id)
                            },
                            onReject = {
                                adminViewModelApi.rejectOrganization(org.id)
                            }
                        )
                    }
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
    var isApproving by remember { mutableStateOf(false) }
    var isRejecting by remember { mutableStateOf(false) }

    // إعادة ضبط اللودر المحلي للكرت فور انتهاء العملية البرمجية
    LaunchedEffect(isGlobalLoading) {
        if (!isGlobalLoading) {
            isApproving = false
            isRejecting = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = org.name.ifBlank { "مؤسسة بدون اسم" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF042A63)
                )
                org.category?.let { type ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(type) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFE8F0FE))
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            org.license?.let {
                if (it.isNotBlank()) Text(text = "رقم الترخيص: $it", fontSize = 13.sp, color = Color.Gray)
            }
            org.phone?.let {
                if (it.isNotBlank()) Text(text = "رقم التواصل: $it", fontSize = 13.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (!org.description.isNullOrBlank()) org.description else "لا يوجد وصف متوفر.",
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            // أزرار اتخاذ القرار (قبول / رفض)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // زر القبول / الاعتماد
                Button(
                    onClick = {
                        isApproving = true
                        onApprove()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isGlobalLoading
                ) {
                    if (isApproving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.Done, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("قبول", fontSize = 14.sp)
                    }
                }

                // زر الرفض
                Button(
                    onClick = {
                        isRejecting = true
                        onReject()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isGlobalLoading
                ) {
                    if (isRejecting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("رفض", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}