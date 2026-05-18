package com.example.volunteerbridge.model

sealed class PasswordResult {
    object Valid : PasswordResult()
    data class Invalid(val message: Int) : PasswordResult()
}