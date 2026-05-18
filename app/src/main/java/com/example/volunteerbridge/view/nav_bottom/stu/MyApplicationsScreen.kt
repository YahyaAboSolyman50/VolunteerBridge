package com.example.volunteerbridge.view.nav_bottom.stu

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.model.classes.ApplicationModel
import com.example.volunteerbridge.viewmodel.ApplicationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationsScreen(
    viewModel: ApplicationsViewModel
) {
    val applications by viewModel.applications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Applications",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background,
                    titleContentColor = colorScheme.onBackground
                )
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colorScheme.primary
                )
            } else if (applications.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "You haven't applied to any opportunities yet.",
                        color = colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(applications, key = { it.appId }) { app ->
                        ApplicationCard(
                            application = app,
                            onCancelClick = {
                                viewModel.cancelApplication(app.appId) { success ->
                                    if (success) {
                                        Toast.makeText(context, "Application canceled successfully", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to cancel application", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationCard(
    application: ApplicationModel,
    onCancelClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val badgeColor = when (application.status.lowercase()) {
        "accepted" -> Color(0xFF4CAF50) // أخضر عند القبول
        "rejected" -> Color(0xFFF44336) // أحمر عند الرفض
        else -> Color(0xFFFF9800)       // برتقالي في حالة الانتظار Pending
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // الجزء العلوي: الأفاتار، عنوان الفرصة، واسم المؤسسة
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // الأفاتار الدائري المماثل لتصميم الـ Home
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = application.orgName.firstOrNull()?.toString() ?: "?",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = application.oppTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = application.orgName,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(12.dp))

            // الجزء السفلي: التحكم بالحالة في الجهة المقابلة لزر الإلغاء تماماً
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // شارة الحالة (Status Badge) أصبحت في اليسار/البداية دائمًا بقالب أنيق ومحدد الحواف
                Surface(
                    color = badgeColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = application.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                // زر إلغاء التقديم يظهر في اليمين/النهاية فقط إذا كانت الحالة "Pending"
                if (application.status.lowercase() == "pending") {
                    TextButton(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF44336)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cancel",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    // مساحة فارغة للمحافظة على التوازن البصري للكارت إذا لم يكن الزر موجوداً
                    Spacer(modifier = Modifier.size(1.dp))
                }
            }
        }
    }
}