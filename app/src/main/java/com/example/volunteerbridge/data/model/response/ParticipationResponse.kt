package com.example.volunteerbridge.data.model.response

data class ParticipationResponse(
    val id: Int,
    val activity_title: String,
    val joined_at: String,
    val status: String,
    val user: Int,
    val activity: Int
)