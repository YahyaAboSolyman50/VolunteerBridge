package com.example.volunteerbridge.data.model.request

data class VolunteerAttendanceRequest(
    val attendance_date: String,
    val hours: Long,
    val notes: String?,
    val participation: Int,
    val recorded_by: Int?
)