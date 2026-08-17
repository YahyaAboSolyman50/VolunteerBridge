package com.example.volunteerbridge.network

import com.example.volunteerbridge.data.model.TokenManager
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject

class TokenAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // منع التكرار اللانهائي إذا فشل التجديد أيضاً
        if (response.responseCount() >= 2) {
            return null
        }

        val refresh = TokenManager.getRefreshToken() ?: return null

        val client = OkHttpClient()
        val body = JSONObject()
            .put("refresh", refresh)
            .toString()
            .toRequestBody("application/json".toMediaType())

        val refreshRequest = Request.Builder()
            .url("https://gazabackendgraduationproject.pythonanywhere.com/api/auth/refresh/")
            .post(body)
            .build()

        return try {
            val refreshResponse = client.newCall(refreshRequest).execute()
            if (refreshResponse.isSuccessful && refreshResponse.body != null) {
                val responseString = refreshResponse.body!!.string()
                val json = JSONObject(responseString)
                val newToken = json.getString("access")

                TokenManager.saveAccessToken(newToken)

                // إعادة المحاولة بالتوكن الجديد تلقائياً
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun Response.responseCount(): Int {
        var result = 1
        var current = priorResponse
        while (current != null) {
            result++
            current = current.priorResponse
        }
        return result
    }
}