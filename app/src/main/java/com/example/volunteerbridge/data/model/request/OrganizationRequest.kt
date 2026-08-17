package com.example.volunteerbridge.data.model.request

import com.google.gson.annotations.SerializedName

/**
 * كائن إرسال بيانات إنشاء/تعديل المؤسسة إلى السيرفر
 * متوافق تماماً مع مخطط Swagger
 */
data class OrganizationRequest(
    @SerializedName("name")
    val name: String,                  // مطلوب * (Max: 255)

    @SerializedName("category")
    val category: String,              // مطلوب * (تأكد من إرسال أحد الخيارات المقبولة في الـ Enum)

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("license")
    val license: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("description")
    val description: String? = null
)