package com.example.volunteerbridge.data.model.response

data class TokenResponse(
    val access: String,
    val refresh: String?,
    val role: String?,
    val user: UserDto?
)

data class UserDto(
    val id: Int,
    val university_id: String?,
    val role: String?,
    val username: String?,
    val organization_id: Int?
)