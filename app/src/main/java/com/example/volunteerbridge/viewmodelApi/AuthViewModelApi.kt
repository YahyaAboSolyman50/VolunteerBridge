package com.example.volunteerbridge.viewmodelApi

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.data.model.TokenManager
import com.example.volunteerbridge.data.model.request.CustomTokenObtainPairRequest
import com.example.volunteerbridge.data.model.request.OrgLoginRequest
import com.example.volunteerbridge.model.UserType
import com.example.volunteerbridge.model.classes.status.AuthState
import com.example.volunteerbridge.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModelApi(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.apiService

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkSavedSession()
    }

    fun login(identifierInput: String, passwordInput: String) {
        val input = identifierInput.trim()
        val password = passwordInput.trim()

        if (input.contains("@")) {
            loginAsOrganization(input, password)
        } else {
            loginAsStudent(input, password)
        }
    }

    fun loginAsStudent(universityId: String, passwordInput: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = CustomTokenObtainPairRequest(
                    university_id = universityId.trim(),
                    password = passwordInput.trim()
                )
                val response = apiService.login(request)
                handleLoginResponse(response, "STUDENT")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(handleError(e))
            }
        }
    }

    fun loginAsOrganization(emailInput: String, passwordInput: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = OrgLoginRequest(
                    email = emailInput.trim(),
                    password = passwordInput.trim()
                )
                val response = apiService.loginOrganization(request)
                handleLoginResponse(response, "ORGANIZATION")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(handleError(e))
            }
        }
    }

    private fun handleLoginResponse(
        response: Response<com.example.volunteerbridge.data.model.response.LoginResponse>,
        defaultRole: String
    ) {
        if (response.isSuccessful && response.body() != null) {
            val loginResponse = response.body()!!
            val accessToken = loginResponse.access ?: ""
            val refreshToken = loginResponse.refresh ?: ""

            if (accessToken.isEmpty()) {
                _authState.value = AuthState.Error("خطأ: التوكن غير موجود في استجابة السيرفر")
                return
            }

            val serverRole = loginResponse.user?.role?.uppercase()
                ?: loginResponse.role?.uppercase()
                ?: defaultRole

            val detectedUserType = mapRoleToUserType(serverRole)

            TokenManager.saveTokensAndRole(accessToken, refreshToken, serverRole)
            _authState.value = AuthState.Success(accessToken, detectedUserType)

        } else {
            val loginErrorCode = response.code()
            val errorBodyString = response.errorBody()?.string()
            _authState.value = AuthState.Error("خطأ $loginErrorCode: $errorBodyString")
        }
    }

    fun saveSessionManually() {
        val currentState = _authState.value
        if (currentState is AuthState.Success) {
            val roleString = when (currentState.userType) {
                is UserType.Admin -> "ADMIN"
                is UserType.Organization -> "ORGANIZATION"
                is UserType.Volunteer -> "VOLUNTEER"
                is UserType.Supervisor -> "SUPERVISOR"
                is UserType.Leader -> "LEADER"
                else -> "STUDENT"
            }
            TokenManager.saveTokensAndRole(currentState.token, getSavedRefreshToken() ?: "", roleString)
        }
    }

    private fun checkSavedSession() {
        val savedToken = TokenManager.getToken()
        val savedTypeStr = TokenManager.getRole()

        if (!savedToken.isNullOrEmpty()) {
            val userType = mapRoleToUserType(savedTypeStr)
            TokenManager.saveTokensAndRole(savedToken, TokenManager.getRefreshToken() ?: "", savedTypeStr)
            _authState.value = AuthState.Success(savedToken, userType)
        } else {
            _authState.value = AuthState.Idle
        }
    }

    private fun mapRoleToUserType(role: String): UserType {
        return when (role.trim().uppercase()) {
            "ADMIN" -> UserType.Admin
            "ORGANIZATION" -> UserType.Organization
            "SUPERVISOR" -> UserType.Supervisor
            "LEADER" -> UserType.Leader
            "VOLUNTEER" -> UserType.Volunteer
            else -> UserType.Student
        }
    }

    fun getSavedToken(): String? {
        return TokenManager.getToken()
    }

    fun getSavedRefreshToken(): String? {
        return TokenManager.getRefreshToken()
    }

    fun saveNewAccessToken(newToken: String) {
        TokenManager.saveAccessToken(newToken)
    }

    fun getSavedUserType(): String {
        return TokenManager.getRole()
    }

    fun logout() {
        TokenManager.clear()
        _authState.value = AuthState.Idle
    }

    private fun handleError(e: Exception): String {
        return e.localizedMessage ?: "حدث خطأ غير متوقع في الاتصال بالشبكة"
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}