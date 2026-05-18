package com.example.volunteerbridge.model

import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.model.classes.Organization
import com.example.volunteerbridge.model.classes.SignupErrors

object AuthValidator {
    fun validateSignupErrors(
        regOrg: Organization,
        confPassword: String
    ): SignupErrors {
        return SignupErrors(
            nameError = if (regOrg.nameOrg.isBlank()) R.string.err_name_req else null,

            emailError = when {
                regOrg.emailOrg.isBlank() -> R.string.err_email_invalid

                !android.util.Patterns.EMAIL_ADDRESS.matcher(regOrg.emailOrg).matches() ->
                    R.string.err_email_invalid

                regOrg.emailOrg.lowercase().contains(".edu") ->
                    R.string.err_email_invalid

                else -> null
            },

            licenseError = if (regOrg.license.isBlank()) R.string.err_license_req else null,

            phoneError = when {
                regOrg.phone.isBlank() -> R.string.err_phone_req
                !"^05[96][0-9]{7}$".toRegex().matches(regOrg.phone) -> R.string.err_phone_format
                else -> null
            },

            passwordError = when {
                regOrg.passwordOrg.length < 8 -> R.string.err_pass_short
                !regOrg.passwordOrg.any { it.isDigit() } -> R.string.err_pass_digit
                !regOrg.passwordOrg.any { it.isUpperCase() } -> R.string.err_pass_upper
                else -> null
            },

            confirmPasswordError = if (regOrg.passwordOrg != confPassword)
                R.string.err_pass_match else null
        )
    }
}
