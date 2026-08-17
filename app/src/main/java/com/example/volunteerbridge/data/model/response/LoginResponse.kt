package com.example.volunteerbridge.data.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access")
    val access: String,

    @SerializedName("refresh")
    val refresh: String,


    @SerializedName("user")
    val user: UserInfo?,

    @SerializedName("role")
    val role: String?
)

data class UserInfo(
    @SerializedName("id")
    val id: Int,

    @SerializedName("university_id")
    val universityId: String?,

    @SerializedName("role")
    val role: String?,

    @SerializedName("username")
    val username: String?,

    @SerializedName("organization_id")
    val organizationId: Int?
)