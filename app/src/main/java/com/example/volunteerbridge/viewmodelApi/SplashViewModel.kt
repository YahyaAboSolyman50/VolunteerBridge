package com.example.volunteerbridge.viewmodelApi

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel

class SplashViewModel(app: Application) : AndroidViewModel(app) {
    private val sharedPref = app.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun isFirstTime(): Boolean {
        return sharedPref.getBoolean("is_first_time", true)
    }

    fun setOnboardingCompleted() {
        sharedPref.edit().putBoolean("is_first_time", false).apply()
    }
}