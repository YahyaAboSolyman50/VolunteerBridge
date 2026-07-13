package com.example.volunteerbridge.data.model.entities.dto


import com.google.gson.annotations.SerializedName

data class ParticipationResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("activity_title")
    val activityTitle: String,
    @SerializedName("joined_at")
    val joinedAt: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("user")
    val user: Int,
    @SerializedName("activity")
    val activity: Int
)

// كائن لاستقبال مجموع الساعات
data class TotalHoursResponse(
    @SerializedName("total_hours")
    val totalHours: Int
)