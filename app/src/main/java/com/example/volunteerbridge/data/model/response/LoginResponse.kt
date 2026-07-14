package com.example.volunteerbridge.data.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access")
    val access: String,

    @SerializedName("refresh")
    val refresh: String
)