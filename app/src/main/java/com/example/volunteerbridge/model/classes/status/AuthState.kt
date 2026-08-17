package com.example.volunteerbridge.model.classes.status

import com.example.volunteerbridge.data.model.response.UserProfileResponse
import com.example.volunteerbridge.model.UserType

sealed class AuthState {
    // الحالة الابتدائية (قبل أي محاولة دخول)
    object Idle : AuthState()

    // حالة التحميل (عند الضغط على تسجيل الدخول والانتظار)
    object Loading : AuthState()

    // حالة النجاح: تحمل بيانات الملف الشخصي للمستخدم بعد تسجيل دخوله بنجاح وجلب بياناته
    data class Success(val token: String, val userType: UserType) : AuthState()

    // حالة الخطأ: تحمل رسالة الخطأ لعرضها للمستخدم في حال فشل الاتصال أو خطأ البيانات
    data class Error(val message: String) : AuthState()
}