package com.example.volunteerbridge.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// إعدادات ألوان الوضع الداكن
private val DarkColorScheme = darkColorScheme(
    primary = TealPrimary,            // التركواز المضيء
    secondary = PurpleAccent,
    background = DarkBlueBg,          // الخلفية الزرقاء الداكنة جداً
    surface = CardBgDark,             // الكروت الداكنة
    onPrimary = Color.Black,
    onBackground = TextWhite,
    onSurface = TextWhite
)

// إعدادات ألوان الوضع الفاتح (مع خلفية بيضاء)
private val LightColorScheme = lightColorScheme(
    primary = TealPrimaryDark,        // درجة أغمق قليلاً للوضوح على الأبيض
    secondary = PurpleAccent,
    background = WhiteBg,             // الخلفية البيضاء
    surface = CardBgLight,            // كروت رمادية فاتحة جداً للتميز
    onPrimary = Color.White,
    onBackground = TextDark,          // نصوص داكنة جداً
    onSurface = TextDark,
    outline = TealPrimaryDark.copy(alpha = 0.5f) // حدود الحقول
)

@Composable
fun VolunteerBridgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // تم تعطيل الألوان الديناميكية للحفاظ على ألوان الهوية البصرية لمشروعك
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}