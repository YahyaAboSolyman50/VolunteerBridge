package com.example.volunteerbridge.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrgStatusBadge(isVerified: Boolean) {
    val colorScheme = MaterialTheme.colorScheme
    val accentColor = colorScheme.primary

    Surface(
        color = if (isVerified  ) accentColor.copy(alpha = 0.15f) else colorScheme.errorContainer,
        shape = RoundedCornerShape(50.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.Default.Info,
                contentDescription = null,
                tint = if (isVerified) accentColor else colorScheme.error,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isVerified) "Verified Account" else "Pending Approval",
                color = if (isVerified) accentColor else colorScheme.error,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}