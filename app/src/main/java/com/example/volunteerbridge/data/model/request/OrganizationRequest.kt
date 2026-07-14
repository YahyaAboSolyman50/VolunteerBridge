package com.example.volunteerbridge.data.model.request

/**
 * كائن إرسال البيانات الخاص بالمؤسسات (Organizations) إلى السيرفر
 * متوافق مع الحقول المطلوبة والإجبارية في Django Swagger
 */
data class OrganizationRequest(
    val name: String,         // اسم المؤسسة (مطلوب)
    val category: String,     // تصنيف المؤسسة (مطلوب)
    val phone: String?,       // رقم الهاتف (اختياري / قابل للحذف)
    val email: String?,       // البريد الإلكتروني (اختياري / قابل للحذف)
    val address: String?,     // العنوان (اختياري / قابل للحذف)
    val description: String?  // وصف المؤسسة (اختياري / قابل للحذف)
)