package com.example.volunteerbridge.view.onboardingScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.model.classes.OnboardingData
import kotlinx.coroutines.launch

// ===================== Onboarding Screen =====================
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnboardingData(R.drawable.page_1, R.string.description_app),
        OnboardingData(R.drawable.page_2, R.string.page_2),
        OnboardingData(R.drawable.page_3, R.string.page_3)
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })

    // تدرج لوني خفيف للخلفية
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(padding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                OnboardingPageDesign(item = pages[page])
            }

            OnboardingIndicator(
                pagerState = pagerState,
                pageCount = pages.size,
                onFinished = onFinished,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// ===================== Onboarding Page (النصوص فقط) =====================
@Composable
fun OnboardingPageDesign(item: OnboardingData) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // لجعل الكلام في منتصف الشاشة تماماً
    ) {
        // أيقونة صغيرة اختيارية أو شعار التطبيق بشكل خفيف جداً في الأعلى
        Icon(
            painter = painterResource(id = com.example.volunteerbridge.app.R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .alpha(0.1f),
            tint = colorScheme.primary
        )

        Spacer(modifier = Modifier.height(40.dp))

        // العنوان الرئيسي أو النص الشارح
        Text(
            text = stringResource(item.title),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 45.sp,
                letterSpacing = (-1).sp
            ),
            textAlign = TextAlign.Center,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        // خط زينة صغير تحت النص ليعطي طابعاً جمالياً
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(colorScheme.primary.copy(alpha = 0.5f))
        )

        Spacer(modifier = Modifier.height(100.dp)) // موازنة المساحة لزر التنقل في الأسفل
    }
}
// ===================== Navigation Button =====================
@Composable
fun NavigationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String? = null,
    icon: Boolean = false,
    colors: ButtonColors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = colors,
        shape = shape
    ) {
        if (icon) {
         if (enabled) {
             Icon(
                 imageVector = if (isRtl) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack,
                 contentDescription = null,
                 tint = contentColor
             )
         }
        } else {
            Text(text ?: "", color = contentColor)
        }
    }
}

// ===================== Onboarding Indicator =====================
@Composable
fun OnboardingIndicator(
    pagerState: PagerState,
    pageCount: Int,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // مؤشرات الصفحات (Dots)
        Row(
            modifier = Modifier.padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                val width = if (isSelected) 25.dp else 10.dp // النقطة الحالية تكون أعرض
                val color = if (isSelected) colorScheme.primary else colorScheme.primary.copy(alpha = 0.3f)

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .height(10.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        // أزرار التنقل (Next & Previous)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // زر "Back" - يظهر فقط إذا لم نكن في الصفحة الأولى
            if (pagerState.currentPage > 0) {
                TextButton(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    }
                ) {
                    Text(
                        text = "Back",
                        color = colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(60.dp)) // توازن المسافات
            }

            // زر "Next" أو "Join Now"
            val isLastPage = pagerState.currentPage == pageCount - 1
            Button(
                onClick = {
                    if (!isLastPage) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinished()
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(56.dp)
                    .width(if (isLastPage) 160.dp else 100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
            ) {
                Text(
                    text = if (isLastPage) "Join Now" else "Next",
                    color = colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                if (!isLastPage) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}