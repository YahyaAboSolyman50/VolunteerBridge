package com.example.volunteerbridge.data.model.request

data class ActivityRequest(
    val title: String,
    val description: String,
    val category: String?,
    val location: String,
    val start_date: String?,
    val end_date: String?,
    val registration_deadline: String?, // الموعد النهائي للتسجيل (مقبل للحذف/Null)
    val volunteer_limit: Long?,
    val status: String?,                // حالة الفرصة عند الإنشاء
    val hours: Long?
)