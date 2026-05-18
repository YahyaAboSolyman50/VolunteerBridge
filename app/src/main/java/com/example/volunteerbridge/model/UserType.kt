package com.example.volunteerbridge.model

sealed class UserType {
    object Student : UserType()
    object Organization : UserType()
    object Loading : UserType()
    object Admin : UserType()
    object Guest : UserType()
    object Error : UserType()

}

