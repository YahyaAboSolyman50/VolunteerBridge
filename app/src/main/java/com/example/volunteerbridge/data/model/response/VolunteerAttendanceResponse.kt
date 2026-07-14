package com.example.volunteerbridge.data.model.response

data class VolunteerAttendanceResponse(
    val id: Int,
    val volunteer_name: String,
    val activity_title: String,
    val recorded_by_name: String,
    val attendance_date: String,
    val hours: Long,
    val notes: String?,
    val created_at: String,
    val participation: Int,
    val recorded_by: Int?
)