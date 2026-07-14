package com.example.volunteerbridge.data.model.status

data class SignupErrors(
    var nameError: Int? = null,
    var emailError: Int? = null,
    var licenseError: Int? = null,
    var phoneError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null
){
    fun hasError(): Boolean {
        return nameError != null ||
                emailError != null ||
                licenseError != null ||
                phoneError != null ||
                passwordError != null ||
                confirmPasswordError != null
    }
}