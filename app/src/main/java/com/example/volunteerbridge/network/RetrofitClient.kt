package com.example.volunteerbridge.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.volunteerbridge.data.model.TokenManager

object RetrofitClient {

    private const val BASE_URL =
        "https://gazabackendgraduationproject.pythonanywhere.com/api/"

    private val client by lazy {
        OkHttpClient.Builder()
            // إضافة التوكن تلقائياً لكل طلب عبر Interceptor بسيط وآمن تماماً
            .addInterceptor(Interceptor { chain ->
                val originalRequest = chain.request()
                val token = TokenManager.getToken()
                Log.d("API_TOKEN_CHECK", "Sending Token: $token")
                val newRequest = if (!token.isNullOrEmpty()) {
                    originalRequest.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else {
                    originalRequest
                }
                chain.proceed(newRequest)
            })
            // استخدام Authenticator المخصص لتجديد التوكن بأمان مطلق بدون أخطاء closed
            .authenticator(TokenAuthenticator())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val apiService: VolunteerBridgeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VolunteerBridgeApiService::class.java)
    }
}