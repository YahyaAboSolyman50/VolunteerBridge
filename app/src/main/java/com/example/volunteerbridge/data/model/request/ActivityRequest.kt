package com.example.volunteerbridge.data.model.request

import com.google.gson.annotations.SerializedName

data class ActivityRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String? = null,
    @SerializedName("location") val location: String,
    @SerializedName("start_date") val startDate: String? = null,              // YYYY-MM-DD
    @SerializedName("end_date") val endDate: String? = null,                  // YYYY-MM-DD
    @SerializedName("registration_deadline") val registrationDeadline: String? = null, // YYYY-MM-DD
    @SerializedName("volunteer_limit") val volunteerLimit: Int? = null,      // تم تعديلها لتطابق الـ Swagger (Integer/Long)
    @SerializedName("status") val status: String? = "active",
    @SerializedName("hours") val hours: Int? = null
)