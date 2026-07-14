package com.example.volunteerbridge.data.model.request

data class DailyActivityLogRequest(
    val activity_type: String,
    val title: String,
    val description: String?,
    val date: String,
    val notes: String?,
    val organization: Int?
)