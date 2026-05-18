package com.example.volunteerbridge.view.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.volunteerbridge.model.classes.Organization
import com.example.volunteerbridge.model.classes.status.UiState
import com.example.volunteerbridge.viewmodel.AuthViewModel
import com.example.volunteerbridge.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    authViewModel: AuthViewModel,
    adminViewModel: AdminViewModel,
    onLogoutSuccess: () -> Unit
) {
    val context = LocalContext.current

    // 🔍 مراقبة قائمة البيانات وقائمة الـ UiState من الـ ViewModel
    val pendingOrgs by adminViewModel.pendingOrganizations
    val adminUiState by adminViewModel.adminUiState.collectAsState()

    // تحديد إذا كانت هناك عملية اعتماد قيد التحميل حالياً لتجميد الأزرار
    val isOperationLoading = adminUiState is UiState.Loading

    // 🔄 بدء جلب البيانات فور بناء الشاشة
    LaunchedEffect(Unit) {
        adminViewModel.fetchPendingOrganizationsForAdmin()
    }

    // ⚡ مراقبة تأثيرات الـ UiState (إظهار توست عند النجاح أو الخطأ وتصفير الحالة)
    LaunchedEffect(adminUiState) {
        when (adminUiState) {
            is UiState.Success -> {
                Toast.makeText(context, "تم اعتماد وتوثيق المؤسسة بنجاح ✔️", Toast.LENGTH_SHORT).show()
                adminViewModel.resetAdminUiState() // تصفير الحالة للعودة إلى Idle
            }
            is UiState.Error -> {
                val errorMessage = (adminUiState as UiState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                adminViewModel.resetAdminUiState()
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
                            authViewModel.logout()
                            onLogoutSuccess()
                        },
                        enabled = !isOperationLoading // منع الخروج أثناء معالجة البيانات
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
                                adminViewModel.adminApproveOrganization(org.uid)
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
    org: Organization,
    isGlobalLoading: Boolean,
    onApprove: () -> Unit
) {
    var isThisCardLoading by remember { mutableStateOf(false) }

    // تصفير لودر الكرت فور انتهاء العملية العامة
    if (!isGlobalLoading) {
        isThisCardLoading = false
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
                Text(text = org.nameOrg, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF042A63))
                SuggestionChip(
                    onClick = {},
                    label = { Text(org.orgType ?: "مؤسسة") },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFE8F0FE))
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "رقم الترخيص: ${org.license}", fontSize = 13.sp, color = Color.Gray)
            Text(text = "رقم التواصل: ${org.phone}", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = org.description ?: "لا يوجد وصف متوفر.", fontSize = 14.sp, color = Color.DarkGray, maxLines = 3)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    isThisCardLoading = true
                    onApprove()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(8.dp),
                enabled = !isGlobalLoading // يعطل كافة الكروت الأخرى لمنع التعليق
            ) {
                if (isThisCardLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.Done, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("موافقة واعتماد التوثيق فوراً", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}