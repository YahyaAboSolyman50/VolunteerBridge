package com.example.volunteerbridge.model.classes

data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "", // "Student" or "Organization"
    val totalHours: Int = 0,
    val faculty: String = "",
    val level: Int = 0,
    val major: String = "",
    val studentId: String = "",
    val phone: String = ""
)
