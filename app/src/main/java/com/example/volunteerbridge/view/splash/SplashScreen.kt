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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.model.classes.status.AuthState
import com.example.volunteerbridge.screens.Screen
import com.example.volunteerbridge.viewmodelApi.SplashViewModel
import com.example.volunteerbridge.viewmodelApi.AuthViewModelApi
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel,
    authViewModel: AuthViewModelApi
) {
    var startAnimation by remember { mutableStateOf(false) }

    // نراقب حالة الجلسة والتسجيل عبر الـ AuthState الجديد
    val authState by authViewModel.authState.collectAsState()

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000)
    )

    LaunchedEffect(Unit) {
        startAnimation = true

        // ننتظر انتهاء وقت الأنيميشن وعملية فحص الجلسة (التي تبدأ تلقائياً في init الـ ViewModel)
        delay(2500)

        // تحديد الوجهة بناءً على حالة التشغيل لأول مرة وحالة الـ Authentication
        if (viewModel.isFirstTime()) {
            navController.navigate(Screen.OnboardingScreen.rout) {
                popUpTo(Screen.SplashScreen.rout) { inclusive = true }
            }
        } else {
            // نتحقق من الـ AuthState الحالي للجلسة المحفوظة
            when (authState) {
                is AuthState.Success -> {
                    // إذا وجدنا جلسة صحيحة وتم تحميل الملف الشخصي بنجاح، نتوجه للرئيسية فوراً
                    navController.navigate(Screen.HomeScreen.rout) {
                        popUpTo(Screen.SplashScreen.rout) { inclusive = true }
                    }
                }
                else -> {
                    // في الحالات الأخرى (Idle أو Error أو Loading مستمر بدون بيانات)، نتوجه لصفحة تسجيل الدخول
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
            .background(backgroundGradient),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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

            Text(
                text = stringResource(R.string.app_name),
                color = colorScheme.primary,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.alpha(alpha)
            )

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

        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .size(30.dp)
                .alpha(alpha.coerceAtMost(0.6f)),
            color = colorScheme.primary,
            strokeWidth = 2.dp
        )
    }
}