package com.example.volunteerbridge.data.model.response
import com.google.gson.annotations.SerializedName

/**
 * كائن استقبال تفاصيل الفرصة التطوعية بالكامل من السيرفر
 */
data class ActivityResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String?,
    @SerializedName("location") val location: String,
    @SerializedName("start_date") val start_date: String?,
    @SerializedName("end_date") val end_date: String?,
    @SerializedName("registration_deadline") val registrationDeadline: String?, // الحقل الجديد المضاف
    @SerializedName("volunteer_limit") val volunteerLimit: Long?,
    @SerializedName("applicants_count") val applicantsCount: Long?,             // عدد المتقدمين الحاليين
    @SerializedName("status") val status: String?,                            // حالة الفرصة (مثلاً Active, Closed)
    @SerializedName("hours") val hours: Long?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("organization") val organization: Int                     // أصبح readOnly ويأتي كـ ID رقمي
)