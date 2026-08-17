package com.example.volunteerbridge.data.model.response

import com.google.gson.annotations.SerializedName

data class ParticipationResponse(
    val id: Int,
    val user: Int,
    @SerializedName("student_name")
    val studentName: String?,
    @SerializedName("university_id")
    val universityId: String?,
    val activity: Int,
    @SerializedName("activity_title")
    val activityTitle: String,
    val status: String,
    @SerializedName("joined_at")
    val joinedAt: String
)