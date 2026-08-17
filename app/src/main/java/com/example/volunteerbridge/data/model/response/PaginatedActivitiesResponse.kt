package com.example.volunteerbridge.data.model.response

import com.google.gson.annotations.SerializedName

data class PaginatedActivityResponse(
    @SerializedName("count")
    val count: Int? = null,

    @SerializedName("next")
    val next: String? = null,

    @SerializedName("previous")
    val previous: String? = null,

    // 👈 تأكد هل اسمها في السيرفر results أم data أم items؟
    @SerializedName("results")
    val results: List<ActivityResponse>? = null
)