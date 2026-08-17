package com.example.volunteerbridge.data.model.response

import com.google.gson.annotations.SerializedName

/**
 * كائن استقبال بيانات المؤسسة من السيرفر
 * يحتوي على جميع الحقول المرجعة من Swagger بما فيها ReadOnly و Nullable
 */
data class OrganizationResponse(
    @SerializedName("id")
    val id: Int,                        // ReadOnly (تلقائي من السيرفر)

    @SerializedName("name")
    val name: String,                   // اسم المؤسسة

    @SerializedName("category")
    val category: String,               // تصنيف المؤسسة

    @SerializedName("email")
    val email: String?,                 // البريد الإلكتروني (Nullable)

    @SerializedName("phone")
    val phone: String?,                 // رقم الهاتف (Nullable)

    @SerializedName("address")
    val address: String?,               // العنوان (Nullable)

    @SerializedName("description")
    val description: String?,           // التفاصيل والوصف (Nullable)

    @SerializedName("license")
    val license: String?,               // التلخيص/الترخيص (Nullable)

    @SerializedName("verified")
    val verified: Boolean? = true,     // حالة التوثيق

    @SerializedName("status")
    val status: String?,                // حالة الحساب (Enum)

    @SerializedName("created_at")
    val createdAt: String?              // ReadOnly (تاريخ الإنشاء)
)