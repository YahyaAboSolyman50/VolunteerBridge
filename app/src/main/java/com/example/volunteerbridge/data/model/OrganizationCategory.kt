package com.example.volunteerbridge.data.model

enum class OrganizationCategory(val value: String) {
    NGO("ngo"),
    GOVERNMENT("government"),
    INTERNATIONAL("international");

    companion object {
        val list = listOf(NGO.value, GOVERNMENT.value, INTERNATIONAL.value)
    }
}