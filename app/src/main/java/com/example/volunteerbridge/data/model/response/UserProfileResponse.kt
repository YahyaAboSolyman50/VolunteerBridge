package com.example.volunteerbridge.data.model.response


import com.google.gson.annotations.SerializedName

/**
 * كائن استقبال بيانات الملف الشخصي للطالب أو المستخدم الحالي من السيرفر
 * الـ Endpoint: GET /api/auth/profile/
 */
data class UserProfileResponse(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("username")
    val name: String = "",                                   // اسم المستخدم (الاسم الكامل)

    @SerializedName("university_id")
    val universityId: String = "",                           // الرقم الجامعي

    @SerializedName("email")
    val email: String = "",                                  // الإيميل

    @SerializedName("role")
    val role: String = "",                                   // الدور: admin, student, organization

    @SerializedName("phone")
    val phone: String? = "",                                 // رقم الهاتف (nullable)

    @SerializedName("profile_image")
    val profileImage: String? = "",                           // رابط الصورة الشخصية (nullable)

    @SerializedName("total_hours")
    val totalCompletedHours: String = "0",                   // إجمالي الساعات المنجزة (تأتي كـ String من الـ API)

    @SerializedName("required_hours")
    val requiredHours: String = "0",                         // الساعات المطلوبة للتخرج

    @SerializedName("remaining_hours")
    val remainingHours: String = "0",                         // الساعات المتبقية

    @SerializedName("completion_percentage")
    val completionPercentage: String = "0%"                  // النسبة المئوية للإنجاز جاهزة من السيرفر (مثل "45.5%")
)