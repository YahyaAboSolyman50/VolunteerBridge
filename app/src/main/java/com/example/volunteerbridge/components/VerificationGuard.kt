package com.example.volunteerbridge.components


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VerificationGuard(
    isVerified: Boolean,
    title: String = "الخصائص مقيدة حالياً",
    message: String = "حسابكم قيد المراجعة والتدقيق من قبل شؤون الطلاب في الجامعة. بمجرد التوثيق، ستتمكن من استخدام هذه الميزة فوراً.",
    content: @Composable () -> Unit
) {
    if (isVerified) {
        // ✅ إذا كانت المؤسسة موثقة، اعرض محتوى الشاشة الأصلي فوراً
        content()
    } else {
        // 🔒 إذا كانت غير موثقة، اعرض واجهة القفل الموحدة لمنع التكرار
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(76.dp),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}