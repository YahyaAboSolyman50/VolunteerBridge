package com.example.volunteerbridge.model.classes.status

sealed class ErrorState {
    object Idle : ErrorState()
    object Success : ErrorState()
    data class Error(val message: String) : ErrorState()
}