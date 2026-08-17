package com.example.volunteerbridge.model

import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.data.model.request.OrganizationRequest
import com.example.volunteerbridge.model.classes.SignupErrors

object AuthValidator {
    fun validateSignupErrors(
        regOrg: OrganizationRequest,
        confPassword: String
    ): SignupErrors {

        // 1. التحقق من حقل البريد الإلكتروني (بما أنه Nullable)
        val emailValue = regOrg.email ?: ""
        val emailErr = when {
            emailValue.isBlank() -> R.string.err_email_invalid
            !android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() -> R.string.err_email_invalid
            // التحقق من البريد التعليمي .edu إذا كنت لا تريد السماح للمؤسسات بالتسجيل به
            emailValue.lowercase().contains(".edu") -> R.string.err_email_invalid
            else -> null
        }

        // 2. التحقق من رقم الهاتف (بما أنه Nullable)
        val phoneValue = regOrg.phone ?: ""
        val phoneErr = when {
            phoneValue.isBlank() -> R.string.err_phone_req
            !"^05[96][0-9]{7}$".toRegex().matches(phoneValue) -> R.string.err_phone_format
            else -> null
        }

        // 3. التحقق من كلمة المرور
        // ملاحظة: إذا كان الـ API يتطلب إرسال كلمة المرور مع طلب إنشاء المؤسسة،
        // يجب عليك إضافة حقل password داخل كلاس OrganizationRequest أولاً.
        // قمنا هنا بافتراض وجود حقل password في الطلب، أو يمكنك التحقق من الـ password الممرر مباشرة.
        val passwordErr = when {
            confPassword.length < 8 -> R.string.err_pass_short
            !confPassword.any { it.isDigit() } -> R.string.err_pass_digit
            !confPassword.any { it.isUpperCase() } -> R.string.err_pass_upper
            else -> null
        }

        return SignupErrors(
            // الاسم حقل إجباري
            nameError = if (regOrg.name.isBlank()) R.string.err_name_req else null,

            emailError = emailErr,

            // تم إزالة حقل الترخيص (license) لعدم وجوده في موديل الـ API الخاص بك
            licenseError = null,

            phoneError = phoneErr,

            passwordError = passwordErr,

            // التحقق من تطابق التأكيد مع كلمة المرور
            confirmPasswordError = if (confPassword.isBlank()) R.string.err_pass_match else null
        )
    }
}