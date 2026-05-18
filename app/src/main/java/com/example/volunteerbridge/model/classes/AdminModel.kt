package com.example.volunteerbridge.model.classes

import java.util.Date

data class AdminModel(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "admin",
    val createdAt: Long =  System.currentTimeMillis()
)
