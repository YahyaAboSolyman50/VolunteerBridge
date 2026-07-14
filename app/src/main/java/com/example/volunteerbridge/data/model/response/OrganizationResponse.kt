package com.example.volunteerbridge.data.model.response

/**
 * كائن استقبال البيانات الخاص بالمؤسسات (Organizations) من السيرفر
 * يحتوي على جميع الحقول الكاملة بما فيها الحقول التي تحمل وسم (readOnly: true)
 */
data class OrganizationResponse(
    val id: Int,              // الرقم المعرف الفريد للمؤسسة (تلقائي من السيرفر)
    val name: String,         // اسم المؤسسة
    val category: String,     // تصنيف المؤسسة
    val phone: String?,       // رقم الهاتف
    val email: String?,       // البريد الإلكتروني
    val address: String?,     // العنوان
    val description: String?,  // وصف المؤسسة
    val created_at: String    // تاريخ ووقت إنشاء الحساب في النظام
)