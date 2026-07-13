package com.example.volunteerbridge.data.model.entities.dto


import com.google.gson.annotations.SerializedName

data class ActivityResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("category")
    val category: String, // يمكنك تحويلها لـ Enum مخصص داخل التطبيق لاحقاً

    @SerializedName("location")
    val location: String?,

    @SerializedName("start_date")
    val startDate: String?,

    @SerializedName("end_date")
    val endDate: String?,

    @SerializedName("volunteer_limit")
    val volunteerLimit: Int?,

    @SerializedName("hours")
    val hours: Int?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("organization")
    val organization: Int
)

data class ActivityRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("start_date")
    val startDate: String, // بصيغة "YYYY-MM-DD"

    @SerializedName("end_date")
    val endDate: String, // بصيغة "YYYY-MM-DD"

    @SerializedName("volunteer_limit")
    val volunteerLimit: Int,

    @SerializedName("hours")
    val hours: Int,

    @SerializedName("organization")
    val organization: Int // معرف الرقمي للمؤسسة المنشئة
)