package com.example.volunteerbridge.data.model.response

import com.google.gson.annotations.SerializedName

data class ActivityResponse(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("start_date")
    val startDate: String? = null,

    @SerializedName("end_date")
    val endDate: String? = null,

    @SerializedName("registration_deadline")
    val registrationDeadline: String? = null,

    @SerializedName("volunteer_limit")
    val volunteerLimit: Long? = null,

    @SerializedName("applicants_count")
    val applicantsCount: Long? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("hours")
    val hours: Long? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("organization")
    val organization: Int? = null
)