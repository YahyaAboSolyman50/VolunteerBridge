package com.example.volunteerbridge.data.model.entities

import com.google.gson.annotations.SerializedName

// الكلاس المسؤول عن نقل البيانات إلى السيرفر (POST Request)
data class OrganizationCreateDto(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String, // 🔐 ضرورية للإنشاء والتسجيل
    @SerializedName("category") val category: String,    // ngo / government / international
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("description") val description: String
)
// الكلاس المسؤول عن استقبال البيانات من السيرفر (Response)
data class OrganizationResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("address") val address: String,
    @SerializedName("description") val description: String,
    @SerializedName("created_at") val createdAt: String
)

// دالة تحويل مساعدة (Mapper Extension) لتحويل كلاس التطبيق الحالي إلى DTO جاهز للإرسال
//fun Organization.toCreateDto(addressValue: String = "غزة"): OrganizationCreateDto {
//    return OrganizationCreateDto(
//        name = this.nameOrg,
//        category = this.orgType.lowercase(), // تحويلها لحروف صغيرة لتطابق (ngo / government / international)
//        phone = this.phone,
//        email = this.emailOrg,
//        address = addressValue,
//        description = this.description
//    )
//}