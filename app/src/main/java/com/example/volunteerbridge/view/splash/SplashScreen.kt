package com.example.volunteerbridge.view.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.model.UserType
import com.example.volunteerbridge.screens.Screen
import com.example.volunteerbridge.viewmodel.AuthViewModel
import com.example.volunteerbridge.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel,
    authViewModel: AuthViewModel // أضفنا الـ AuthViewModel هنا
) {
    var startAnimation by remember { mutableStateOf(false) }

    // نراقب حالة نوع المستخدم من الـ AuthViewModel
    val userType by authViewModel.userType.collectAsState()

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        // 1. نبدأ بفحص الجلسة المحفوظة فوراً
        authViewModel.checkSavedSession()

        delay(2500) // وقت الأنيميشن

        // 2. تحديد الوجهة بناءً على المنطق
        if (viewModel.isFirstTime()) {
            navController.navigate(Screen.OnboardingScreen.rout) {
                popUpTo(Screen.SplashScreen.rout) { inclusive = true }
            }
        } else {
            // إذا لم تكن المرة الأولى، نتحقق من حالة المستخدم
            when (userType) {
                is UserType.Student, is UserType.Organization -> {
                    // إذا وجدنا جلسة محفوظة (طالب أو منظمة) نذهب للرئيسية فوراً
                    navController.navigate(Screen.HomeScreen.rout) {
                        popUpTo(Screen.SplashScreen.rout) { inclusive = true }
                    }
                }
                else -> {
                    // إذا لم تكن هناك جلسة (Guest أو Error) نذهب للوجن
                    navController.navigate(Screen.LoginScreen.rout) {
                        popUpTo(Screen.SplashScreen.rout) { inclusive = true }
                    }
                }
            }
        }
    }

    Splash(alpha = alphaAnim)
}
@Composable
fun Splash(alpha: Float) {
    val colorScheme = MaterialTheme.colorScheme

    val backgroundGradient = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(
            colorScheme.primary.copy(alpha = 0.1f),
            colorScheme.background,
            colorScheme.primary.copy(alpha = 0.05f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient), // تطبيق التدرج اللوني
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // إضافة بطاقة (Card) حول الشعار ليعطي عمقاً (Elevation)
            Surface(
                modifier = Modifier
                    .size(160.dp)
                    .alpha(alpha)
                    .clip(RoundedCornerShape(32.dp)),
                color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(110.dp),
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // اسم التطبيق مع تحسين الخط والتباعد
            Text(
                text = stringResource(R.string.app_name),
                color = colorScheme.primary,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.alpha(alpha)
            )

            // إضافة شعار بسيط (Slogan) تحت الاسم
            Text(
                text = "Connecting Hearts, Building Bridges",
                color = colorScheme.onBackground.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .alpha(alpha)
            )
        }

        // إضافة مؤشر تحميل بسيط في الأسفل ليوحي بأن التطبيق "يفكر"
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .size(30.dp)
                .alpha(alpha.coerceAtMost(0.6f)), // تقليل شفافيته قليلاً
            color = colorScheme.primary,
            strokeWidth = 2.dp
        )
    }
}
