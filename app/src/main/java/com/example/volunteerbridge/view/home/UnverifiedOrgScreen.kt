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
import com.example.volunteerbridge.viewmodel.OrgViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnverifiedOrgScreen(
    orgViewModel: OrgViewModel,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current

    val isEmailVerified by orgViewModel.isEmailVerified

    // 📧 الاستماع للـ UiState الخاص بالإيميل فقط
    val emailUiState by orgViewModel.emailUiState.collectAsState()
    val isEmailLoading = emailUiState is UiState.Loading

    // مراقبة أخطاء إرسال البريد الصريحة (مثل Too Many Requests)
    LaunchedEffect(emailUiState) {
        if (emailUiState is UiState.Error) {
            val errorMsg = (emailUiState as UiState.Error).message
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            orgViewModel.resetEmailUiState()
        }
    }

    // 🔄 2. تمرير [isEmailVerified] كمفتاح (Key) للـ LaunchedEffect
    // بمجرد أن تصبح قيمته true، سيتوقف الـ block تلقائياً وتنتقل للمرحلة التالية في HomeScreen
    LaunchedEffect(isEmailVerified) {
        if (!isEmailVerified) {
            // إرسال الإيميل عند أول دخول للشاشة فقط
            orgViewModel.sendVerificationEmail()

            // حلقة الفحص الدوري الصامتة
            while (!isEmailVerified) {
                orgViewModel.checkEmailVerificationStatus()
                delay(3000)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تأكيد الحساب والبريد", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { onLogoutClick() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "تسجيل خروج",
                            tint = Color(0xFFD32F2F)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Verification",
                    tint = Color(0xFFEF6C00),
                    modifier = Modifier.size(90.dp)
                )

                Text(
                    text = "تفعيل البريد الإلكتروني مطلوب",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF042A63),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "مرحباً بكم في منصة Volunteer Bridge.\n\n" +
                            "لقد أرسلنا رابط التحقق إلى بريدكم الإلكتروني المسجل.\n\n" +
                            "يرجى الانتقال إلى صندوق الوارد (أو البريد المزعج Spam) والضغط على الرابط لتفعيل حساب المؤسسة.\n\n" +
                            "⏱️ التطبيق سيتعرف على التفعيل تلقائياً وينقلك فوراً بمجرد الضغط على الرابط.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFFEF6C00)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "بانتظار تأكيد الرابط من طرفكم...",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { orgViewModel.checkEmailVerificationStatus() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isEmailLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF042A63))
                ) {
                    if (isEmailLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("تحقق الآن يدوياً 🔄", fontSize = 15.sp)
                    }
                }
            }
        }
    }
}