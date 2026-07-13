package com.example.volunteerbridge.data.model.entities.dto

import com.example.volunteerbridge.data.model.entities.UserModel

import com.google.gson.annotations.SerializedName

// كلاس استقبال بيانات المستخدم عند جلب الملف الشخصي أو بعد تسجيل الدخول
data class UserResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String // admin, supervisor, leader, volunteer
)

// دالة تحويل مساعدة لتحويل الـ DTO القادم من السيرفر إلى UserModel الداخلي الخاص بتطبيقك
//fun UserResponseDto.toUserModel(): UserModel {
//    return UserModel(
//        id = this.id,
//        username = this.username,
//        fullName = "${this.firstName} ${this.lastName}",
//        email = this.email,
//        role = this.role
//    )
//}
