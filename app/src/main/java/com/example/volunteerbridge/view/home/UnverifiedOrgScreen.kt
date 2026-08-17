package com.example.volunteerbridge.view.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import com.example.volunteerbridge.model.classes.status.UiState
import com.example.volunteerbridge.viewmodelApi.OrganizationViewModel
import kotlinx.coroutines.delay

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun UnverifiedOrgScreen(
//    orgViewModel: OrganizationViewModel,
//    onLogoutClick: () -> Unit
//) {
//    val context = LocalContext.current
//
//    val isEmailVerified by orgViewModel.isEmailVerified
//
//    // 📧 مراقبة حالات الـ API للبريد الإلكتروني
//    val emailUiState by orgViewModel.emailUiState.collectAsState()
//    val isEmailLoading = emailUiState is UiState.Loading
//
//    // ⚡ إظهار التنبيهات (Success / Error)
//    LaunchedEffect(emailUiState) {
//        when (val state = emailUiState) {
//            is UiState.Success -> {
//                Toast.makeText(context, state.data, Toast.LENGTH_SHORT).show()
//                orgViewModel.resetEmailUiState()
//            }
//            is UiState.Error -> {
//                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
//                orgViewModel.resetEmailUiState()
//            }
//            else -> {}
//        }
//    }
//
//    // 🔄 حلقة الفحص الدوري الصامتة مع التأكد من عدم التكرار المفرط
//    LaunchedEffect(isEmailVerified) {
//        if (!isEmailVerified) {
//            while (!isEmailVerified) {
//                delay(4000) // فحص كل 4 ثوانٍ
//                orgViewModel.checkEmailVerificationStatus()
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("تأكيد الحساب والبريد", fontWeight = FontWeight.Bold) },
//                actions = {
//                    IconButton(onClick = { onLogoutClick() }) {
//                        Icon(
//                            imageVector = Icons.Default.ExitToApp,
//                            contentDescription = "تسجيل خروج",
//                            tint = Color(0xFFD32F2F)
//                        )
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(24.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(18.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//
//                Icon(
//                    imageVector = Icons.Default.Email,
//                    contentDescription = "Email Verification",
//                    tint = Color(0xFFEF6C00),
//                    modifier = Modifier.size(80.dp)
//                )
//
//                Text(
//                    text = "تفعيل البريد الإلكتروني مطلوب",
//                    fontSize = 22.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF042A63),
//                    textAlign = TextAlign.Center
//                )
//
//                Text(
//                    text = "مرحباً بكم في منصة Volunteer Bridge.\n\n" +
//                            "لقد أرسلنا رابط التحقق إلى بريدكم الإلكتروني المسجل.\n\n" +
//                            "يرجى الانتقال إلى صندوق الوارد (أو البريد المزعج Spam) والضغط على الرابط لتفعيل حساب المؤسسة.\n\n" +
//                            "⏱️ التطبيق سيتعرف على التفعيل تلقائياً وينقلك فوراً عند اكتماله.",
//                    style = MaterialTheme.typography.bodyLarge,
//                    textAlign = TextAlign.Center,
//                    lineHeight = 24.sp,
//                    color = Color.DarkGray
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // مؤشر الانتظار الصامت
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(16.dp),
//                        strokeWidth = 2.dp,
//                        color = Color(0xFFEF6C00)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        text = "جاري التحقق من التفعيل تلقائياً...",
//                        fontSize = 13.sp,
//                        color = Color.Gray,
//                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // زر التحقق اليدوي
//                Button(
//                    onClick = { orgViewModel.checkEmailVerificationStatus() },
//                    modifier = Modifier.fillMaxWidth(),
//                    enabled = !isEmailLoading,
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF042A63))
//                ) {
//                    if (isEmailLoading) {
//                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
//                    } else {
//                        Text("تحقق الآن يدوياً 🔄", fontSize = 15.sp)
//                    }
//                }
//
//                // زر إعـادة إرسال رابط التفعيل
//                OutlinedButton(
//                    onClick = { orgViewModel.sendVerificationEmail() },
//                    modifier = Modifier.fillMaxWidth(),
//                    enabled = !isEmailLoading
//                ) {
//                    Text("إعادة إرسال رابط التفعيل 📧", fontSize = 14.sp)
//                }
//            }
//        }
//    }
//}