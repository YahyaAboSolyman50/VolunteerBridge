package com.example.volunteerbridge.nav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.volunteerbridge.viewmodel.AdminViewModel
import com.example.volunteerbridge.viewmodel.ApplicationsViewModel
import com.example.volunteerbridge.viewmodel.AuthViewModel
import com.example.volunteerbridge.viewmodel.NotViewModel
import com.example.volunteerbridge.viewmodel.OpportunityViewModel
import com.example.volunteerbridge.viewmodel.OrgViewModel
import com.example.volunteerbridge.viewmodel.SplashViewModel
import com.example.volunteerbridge.viewmodel.StudentViewModel
import com.example.volunteerbridge.viewmodel.TaskViewModel

@Composable
fun Nav(context: Context) {
    val navController = rememberNavController()
    val splashViewModel: SplashViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as android.app.Application)
    )
    val oppViewModel: OpportunityViewModel = viewModel()
    val notViewModel: NotViewModel = viewModel()
    val orgViewModel: OrgViewModel = viewModel()
    val stuViewModel: StudentViewModel = viewModel()
    val taskViewModel: TaskViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()
    val appViewModel: ApplicationsViewModel = viewModel()

    LaunchedEffect(Unit) {
        authViewModel.checkSavedSession()
    }
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.rout
    ) {
        composable(Screen.SplashScreen.rout) {
            SplashScreen(navController, splashViewModel, authViewModel)
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
            LoginScreen(navController, authViewModel)
        }
        composable(Screen.SignupScreen.rout) {
            SignupScreen(authViewModel, navController)
        }
        composable(Screen.HomeScreen.rout) {
            HomeScreen(
                navController,
                authViewModel,
                oppViewModel,
                notViewModel,
                orgViewModel,
                stuViewModel,
                taskViewModel,
                adminViewModel,
                appViewModel
            )
        }
    }
}