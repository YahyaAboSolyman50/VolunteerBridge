package com.example.volunteerbridge.data.model.response

data class DailyActivityLogResponse(
    val id: Int,
    val created_by_name: String,
    val organization_name: String,
    val activity_type: String,
    val title: String,
    val description: String?,
    val date: String,
    val notes: String?,
    val created_at: String,
    val created_by: Int?,
    val organization: Int?
)