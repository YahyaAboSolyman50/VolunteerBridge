package com.example.volunteerbridge.data.model.entities

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access")
    val access: String,

    @SerializedName("refresh")
    val refresh: String
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String // admin, student, organization
)