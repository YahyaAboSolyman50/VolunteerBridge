package com.example.volunteerbridge.view.functions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.volunteerbridge.app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale.getDefault

/**
 * غلاف مشترك للشاشات (يوحد الخلفية والعنوان)
 */
@Composable
fun AuthScreenWrapper(
    title: String = "Volunteer Bridge",
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImageDesign()

        // تعديل الظل ليكون طبقة بيضاء خفيفة بدلاً من الأسود ليتناسب مع "اللايت مود"
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.2f)) // تعتيم أبيض خفيف
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else Color.Black
            )

            Spacer(modifier = Modifier.height(30.dp))

            content()
        }
    }
}

/**
 * زر موحد لكل التطبيق لتقليل تكرار Modifier والـ Shape
 */
@Composable
fun PrimaryButton(
    text: String,
    iconRes: Int? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.surface,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        enabled = enabled
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            iconRes?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(text = text, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BackgroundImageDesign() {
    Image(
        painter = painterResource(id = R.drawable.bg), // تأكد من تسمية الصورة بهذا الاسم
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun MyShadow() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
    )
}

@Composable
fun TextFiledDesign(
    state: MutableState<String>,
    label: Int,
    icon: ImageVector,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    placeholder: Int,
    prefix: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    singleLine: Boolean = true
) {
    var showPassword by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {

        OutlinedTextField(
            value = state.value,
            onValueChange = { state.value = it },
            label = { Text(stringResource(label), color = Color.Gray) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color.DarkGray) },
            visualTransformation = if (isPassword && !showPassword)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray,
                errorBorderColor = Color.Red
            ),
            trailingIcon = {
                val iconTint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                if (isPassword) {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            painter = if (showPassword) {
                                painterResource(id = R.drawable.view)
                            } else {
                                painterResource(id = R.drawable.close_eye)
                            },
                            contentDescription = if (showPassword) "Hide password" else "Show password",
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            placeholder = { Text(stringResource(placeholder)) },
            keyboardOptions = keyboardOptions,
            prefix = { Text(prefix) },
            singleLine =  singleLine
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun AuthCard(
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Card(
        colors = CardDefaults.cardColors(
            // تحسين لون الخلفية في الوضع الداكن ليبرز التوهج بشكل أفضل
            containerColor = if (!isDark) Color(0xFFF9F9F9).copy(alpha = 0.95f)
            else Color(0xFF0D1117).copy(alpha = 0.98f)
        ),
        shape = RoundedCornerShape(24.dp),
        // تقليل الظل التقليدي في الوضع الداكن والاعتماد على التوهج اللوني
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 8.dp),
        modifier = Modifier // استخدام الـ modifier الممرر للسماح بتخصيص إضافي
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            // إضافة الحواف المضيئة بالتدرج العمودي
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary, // اللون التركوازي في الأعلى
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        Color.Transparent // يتلاشى في الأسفل تماماً كالصورة
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

@Composable
fun PagePlaceholder(pageName: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = pageName,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

fun formatFullDate(timestamp: Long): String {
    if (timestamp <= 0L) return "Not Set"

    val correctedTimestamp = if (timestamp < 100_000_000_000L) timestamp * 1000 else timestamp

    val date = Date(correctedTimestamp)
    val sdf = SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
    return sdf.format(date)
}

fun formatTimeAgo(timestamp: Long): String {
    if (timestamp <= 0L) return ""

    val correctedTimestamp = if (timestamp < 100_000_000_000L) timestamp * 1000 else timestamp
    val now = System.currentTimeMillis()
    val diff = now - correctedTimestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago" // دقائق
        diff < 86_400_000 -> "${diff / 3_600_000}h ago" // ساعات
        diff < 604_800_000 -> "${diff / 86_400_000}d ago" // أيام
        else -> formatFullDate(correctedTimestamp)
    }
}

