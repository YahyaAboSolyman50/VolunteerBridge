package com.example.volunteerbridge.view.nav_bottom.org.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.viewmodel.AuthViewModel
import com.example.volunteerbridge.viewmodel.OrgViewModel

@Composable
fun OrganizationProfileScreen(
    orgViewModel: OrgViewModel,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val orgModel by orgViewModel.currentOrgData
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        bottomBar = {
            // زر تسجيل الخروج في الأسفل
            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.errorContainer,
                    contentColor = colorScheme.error
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Logout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. الجزء العلوي (Header)
            ProfileHeaderSection(
                name = orgModel?.nameOrg ?: "Organization Name",
                email = orgModel?.emailOrg ?: "email@example.com",
                isVerified = orgModel?.verified ?: false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. محتوى الملف الشخصي
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                // عنوان القسم
                Text(
                    "Organization Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                // كارد المعلومات الأساسية
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        InfoRow(Icons.Default.List, "Org Type", orgModel?.orgType ?: "Not Specified")
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 0.5.dp,
                            color = colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        InfoRow(Icons.Default.Phone, "Phone", orgModel?.phone ?: "No Phone")
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 0.5.dp,
                            color = colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        InfoRow(Icons.Default.Lock, "License ID", orgModel?.license ?: "N/A")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. قسم الوصف (About)
                Text(
                    "About Us",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Text(
                        text = if (orgModel?.description.isNullOrEmpty())
                            "No description provided for this organization."
                        else orgModel?.description!!,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // زر تعديل الملف الشخصي
                OutlinedButton(
                    onClick = onEditProfileClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colorScheme.primary)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Edit Profile", color = colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ProfileHeaderSection(name: String, email: String, isVerified: Boolean) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(colorScheme.primary.copy(alpha = 0.12f), Color.Transparent)
                )
            )
            .padding(top = 40.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // صورة البروفايل مع علامة التوثيق
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(110.dp),
                shape = CircleShape,
                color = colorScheme.primary.copy(alpha = 0.1f),
                border = BorderStroke(2.dp, colorScheme.primary)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = colorScheme.primary
                    )
                }
            }

            if (isVerified) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    color = colorScheme.background,
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = colorScheme.onBackground
        )

        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )
    }
}