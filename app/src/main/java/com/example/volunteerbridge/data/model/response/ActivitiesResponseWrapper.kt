package com.example.volunteerbridge.data.model.response

import com.google.gson.annotations.SerializedName

data class ActivitiesResponseWrapper(
    @SerializedName("results")
    val results: List<ActivityResponse>?
)