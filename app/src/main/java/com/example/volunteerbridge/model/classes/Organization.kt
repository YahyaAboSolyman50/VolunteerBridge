    package com.example.volunteerbridge.model.classes

    data class Organization(
        val uid: String = "",
        val nameOrg: String = "",
        val emailOrg: String = "",
        val passwordOrg: String = "",
        val license: String = "",
        val phone: String = "",
        val orgType: String = "", // "" (NGO), "" (Gov), "" (International)

        val verified: Boolean = false,
        val role: String = "organization",

        val description: String = "",
        val status: String = "pending"
    )
