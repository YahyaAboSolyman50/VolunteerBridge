package com.example.volunteerbridge.data.model.request

import com.google.gson.annotations.SerializedName

data class AdminLogin(
    @SerializedName("username") // أو "email" حسب ما يطلبه الـ Swagger في endpoint الـ login
    val username: String,

    @SerializedName("password")
    val password: String
)