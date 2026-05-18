package com.example.volunteerbridge.model.classes.status

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errorResId: Int) : ValidationResult()
}