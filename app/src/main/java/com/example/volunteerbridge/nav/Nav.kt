package com.example.volunteerbridge.nav

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.volunteerbridge.screens.Screen
import com.example.volunteerbridge.view.home.HomeScreen
import com.example.volunteerbridge.view.login.LoginScreen
import com.example.volunteerbridge.view.onboardingScreen.OnboardingScreen
import com.example.volunteerbridge.view.signup.SignupScreen
import com.example.volunteerbridge.view.splash.SplashScreen
import com.example.volunteerbridge.viewmodelApi.AuthViewModelApi
import com.example.volunteerbridge.viewmodelApi.NotViewModel
import com.example.volunteerbridge.viewmodelApi.SplashViewModel
import com.example.volunteerbridge.viewmodelApi.ActivityViewModel
import com.example.volunteerbridge.viewmodelApi.AdminViewModelApi
import com.example.volunteerbridge.viewmodelApi.AttendanceViewModel
import com.example.volunteerbridge.viewmodelApi.OrganizationViewModel
import com.example.volunteerbridge.viewmodelApi.ParticipationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Nav(context: Context) {
    val navController = rememberNavController()
    val splashViewModel: SplashViewModel = viewModel()
    val authViewModelApi: AuthViewModelApi = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as android.app.Application)
    )

    val notViewModel: NotViewModel = viewModel()
    val activityViewModel: ActivityViewModel = viewModel()
    val adminViewModel: AdminViewModelApi = viewModel()
    val orgApiViewModel: OrganizationViewModel = viewModel()
    val organizationViewModel: OrganizationViewModel = viewModel()
    val studentViewModel: com.example.volunteerbridge.viewmodelApi.StudentViewModel = viewModel()
    val participationViewModel: ParticipationViewModel = viewModel()
    val attendanceViewModel: AttendanceViewModel = viewModel()

    // تم حذف الـ LaunchedEffect الذي كان يستدعي checkSavedSession لأنها تعمل تلقائياً الآن في الـ init الخاص بالـ ViewModel

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.rout
    ) {
        composable(Screen.SplashScreen.rout) {
            SplashScreen(navController, splashViewModel, authViewModelApi)
        }
        composable(Screen.OnboardingScreen.rout) {
            OnboardingScreen(onFinished = {
                splashViewModel.setOnboardingCompleted()
                navController.navigate(Screen.LoginScreen.rout) {
                    popUpTo("onboarding_route") { inclusive = true }
                }
            })
        }
        composable(Screen.LoginScreen.rout) {
            LoginScreen(navController, authViewModelApi)
        }
        composable(Screen.SignupScreen.rout) {
            SignupScreen(orgApiViewModel, navController)
        }
        composable(Screen.HomeScreen.rout) {
            HomeScreen(
                navController,
                authViewModelApi = authViewModelApi,
                notViewModel = notViewModel,
                activityViewModel = activityViewModel,
                adminViewModel = adminViewModel,
                organizationViewModel =organizationViewModel,
                studentViewModel = studentViewModel,
                participationViewModel=participationViewModel,
                attendanceViewModel=attendanceViewModel

            )
        }
    }
}