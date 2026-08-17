package com.example.volunteerbridge.data.model.request

import com.google.gson.annotations.SerializedName

data class OrgLoginRequest(
    @SerializedName("email") // أو "email" حسب ما يطلبه الـ Swagger في endpoint الـ login
    val email: String,

    @SerializedName("password")
    val password: String
)