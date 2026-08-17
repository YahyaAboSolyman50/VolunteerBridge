package com.example.volunteerbridge.model

sealed class UserType {
    object Student : UserType()
    object Volunteer : UserType()
    object Supervisor : UserType()
    object Leader : UserType()
    object Organization : UserType()
    object Admin : UserType()
    object Guest : UserType()
    object Loading : UserType()
    object Error : UserType()
}

