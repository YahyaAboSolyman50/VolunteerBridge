package com.example.volunteerbridge.data.model.response

data class GenericReportResponse(
    val report_title: String?,
    val generated_at: String?,
    val data: List<Map<String, Any>>?
)