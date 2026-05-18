package com.example.volunteerbridge.screens

sealed class Screen(val rout:String) {
    object SplashScreen:Screen("splash_screen")
    object OnboardingScreen:Screen("onboarding_screen")
    object LoginScreen:Screen("login_screen")
    object SignupScreen:Screen("signup_screen")

    object HomeScreen:Screen("home_screen")
}