package com.example.volunteerbridge.view.nav_bottom.stu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(
    stuViewModel: StudentViewModel,
    onLogoutClick: () -> Unit // دالة لتوجيه المستخدم لشاشة تسجيل الدخول بعد الـ Logout
) {
    val colorScheme = MaterialTheme.colorScheme
    val userModel by stuViewModel.currentUserData

    // حساب نسبة التقدم في الساعات التطوعية (الهدف الافتراضي 50 ساعة)
    val targetHours = 50f
    val currentHours = userModel?.totalHours?.toFloat() ?: 0f
    val progressValue = (currentHours / targetHours).coerceIn(0f, 1.0f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onBackground
                )
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()), // ✨ إضافة إمكانية التمرير للحماية من الـ Overflow
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1️⃣ صورة البروفايل الرمزية (Avatar) مع أول حرف من اسم الطالب
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userModel?.name?.firstOrNull()?.toString()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // اسم الطالب الرئيسي
            Text(
                text = userModel?.name ?: "Loading...",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = colorScheme.onBackground
            )

            // نوع الحساب الشرفي
            Surface(
                color = colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Text(
                    text = "VOLUNTEER STUDENT",
                    color = colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2️⃣ بطاقة إحصائيات الساعات التطوعية (تطابق الهوية البصرية لشاشة الـ Home)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Volunteer Badges & Progress",
                                style = MaterialTheme.typography.labelMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$currentHours / ${targetHours.toInt()} Hours Completed",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700), // لون ذهبي للأوسمة
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // شريط التقدم الأنيق
                    LinearProgressIndicator(
                        progress = progressValue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = colorScheme.primary,
                        trackColor = colorScheme.surfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3️⃣ كارت بيانات الطالب الأكاديمية والشخصية
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileInfoRow(
                        icon = Icons.Default.Person,
                        label = "University ID",
                        value = userModel?.studentId ?: "N/A"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colorScheme.outline.copy(alpha = 0.08f))

                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email Address",
                        value = userModel?.email ?: "N/A"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colorScheme.outline.copy(alpha = 0.08f))

                    ProfileInfoRow(
                        icon = Icons.Default.List,
                        label = "Institution",
                        value = "Islamic University of Gaza"
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign Out",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )
        }
    }
}