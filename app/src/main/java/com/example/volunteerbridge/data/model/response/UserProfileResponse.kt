package com.example.volunteerbridge.data.model.response


import com.google.gson.annotations.SerializedName

/**
 * كائن استقبال بيانات الملف الشخصي للطالب أو المستخدم الحالي من السيرفر
 * الـ Endpoint: GET /api/auth/profile/
 */
data class UserProfileResponse(
    @SerializedName("name") val name: String,                           // الاسم الكامل
    @SerializedName("university_id") val universityId: String,          // الرقم الجامعي
    @SerializedName("email") val email: String,                         // الإيميل
    @SerializedName("role") val role: String,                           // الدور (role): admin, student, organization
    @SerializedName("phone") val phone: String?,                        // رقم الهاتف (nullable)
    @SerializedName("profile_image") val profileImage: String?,         // رابط الصورة الشخصية (nullable)
    @SerializedName("total_completed_hours") val totalCompletedHours: Int // إجمالي الساعات المنجزة (للرسم البياني)
)