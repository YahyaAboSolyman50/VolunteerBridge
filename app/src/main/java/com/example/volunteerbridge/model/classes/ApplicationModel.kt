package com.example.volunteerbridge.model.classes

data class ApplicationModel(
    val appId: String = "",
    val oppId: String = "",
    val studentId: String = "",
    val orgId: String = "",

    val oppTitle: String = "",
    val studentName: String = "",
    val orgName: String = "",

    val status: String = "Pending",   // (Pending, Accepted, Rejected)
    val appliedAt: Long = System.currentTimeMillis()
)
