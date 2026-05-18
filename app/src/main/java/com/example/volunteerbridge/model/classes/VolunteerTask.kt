package com.example.volunteerbridge.model.classes

data class VolunteerTask(
    val taskId: String = "",
    val oppId: String = "",
    val oppTitle: String = "",
    val studentId: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = "Pending", // التقييمات: Pending, In Progress, Completed
    val dueDate: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)

