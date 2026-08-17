package com.example.volunteerbridge.data.model

import android.content.Context

object TokenManager {

    private const val PREF_NAME = "volunteer_bridge_prefs"
    private const val KEY_ACCESS = "auth_token"
    private const val KEY_REFRESH = "refresh_token"
    private const val KEY_ROLE = "user_type" // مفتاح حفظ الرول

    private lateinit var prefs: android.content.SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // تحديث دالة الحفظ الشاملة لتستقبل الرول أيضاً
    fun saveTokensAndRole(access: String, refresh: String, role: String) {
        prefs.edit()
            .putString(KEY_ACCESS, access)
            .putString(KEY_REFRESH, refresh)
            .putString(KEY_ROLE, role)
            .apply()
    }

    // دالة قديمة للاحتفاظ بالتوافقية إذا استدعيت في مكان آخر
    fun saveTokens(access: String, refresh: String,userType: String) {
        prefs.edit()
            .putString(KEY_ACCESS, access)
            .putString(KEY_REFRESH, refresh)
            .putString(KEY_ROLE, userType)
            .apply()
    }

    fun saveAccessToken(access: String) {
        prefs.edit()
            .putString(KEY_ACCESS, access)
            .apply()
    }

    // دالة لحفظ أو تحديث الرول منفصلاً إذا احتجت لذلك
    fun saveRole(role: String) {
        prefs.edit()
            .putString(KEY_ROLE, role)
            .apply()
    }

    fun getToken(): String? =
        prefs.getString(KEY_ACCESS, null)

    fun getRefreshToken(): String? =
        prefs.getString(KEY_REFRESH, null)

    // استرجاع الرول المخزن (القيمة الافتراضية STUDENT)
    fun getRole(): String =
        prefs.getString(KEY_ROLE, "STUDENT") ?: "STUDENT"

    fun clear() {
        prefs.edit().clear().apply()
    }
}