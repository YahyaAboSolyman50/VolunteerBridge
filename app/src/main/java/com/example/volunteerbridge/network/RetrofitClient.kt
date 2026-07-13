package com.example.volunteerbridge.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://gazabackendgraduationproject.pythonanywhere.com/api/"

    val apiService: VolunteerBridgeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VolunteerBridgeApiService::class.java)
    }
}