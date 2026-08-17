package com.example.volunteerbridge.viewmodelApi

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.data.model.response.UserProfileResponse
import com.example.volunteerbridge.network.RetrofitClient
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    // 🎓 بيانات ملف الطالب الشخصي
    val currentUserData = mutableStateOf<UserProfileResponse?>(null)

    // ⏳ حالة التحميل
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // الاحتفاظ بمعرفات الفرص التي تقدم إليها الطالب
    private val _appliedOppIds = mutableStateOf<Set<Int>>(emptySet())
    val appliedOppIds: State<Set<Int>> = _appliedOppIds

    /**
     * 👤 دالة جلب بيانات الملف الشخصي للطالب عبر الـ API
     * @param token التوكن النظيف القادم من SharedPreferences
     */
    fun fetchCurrentStudentProfile() {


        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    currentUserData.value = response.body()
                    Log.d("StudentViewModel", "Student data loaded successfully via API!")
                } else {
                    Log.e("StudentViewModel", "Failed to fetch profile: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("StudentViewModel", "Error fetching student profile: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}