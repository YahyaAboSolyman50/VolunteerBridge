package com.example.volunteerbridge.viewmodelApi

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.data.model.response.OrganizationResponse
import com.example.volunteerbridge.model.classes.status.UiState
import com.example.volunteerbridge.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModelApi(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.apiService
    private val sharedPreferences =
        application.getSharedPreferences("volunteer_bridge_prefs", Context.MODE_PRIVATE)

    // قائمة المؤسسات المعلقة
    private val _pendingOrganizations = mutableStateOf<List<OrganizationResponse>>(emptyList())
    val pendingOrganizations: State<List<OrganizationResponse>> = _pendingOrganizations

    // قائمة الأنشطة
    private val _activities = mutableStateOf<List<com.example.volunteerbridge.data.model.response.ActivityResponse>>(emptyList())
    val activities: State<List<com.example.volunteerbridge.data.model.response.ActivityResponse>> = _activities

    // قائمة التقارير (تعديل النوع هنا ليطابق GenericReportResponse)
    private val _reports = mutableStateOf<List<com.example.volunteerbridge.data.model.response.GenericReportResponse>>(emptyList())
    val reports: State<List<com.example.volunteerbridge.data.model.response.GenericReportResponse>> = _reports

    // حالة الواجهة (Loading, Success, Error)
    private val _adminUiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val adminUiState: StateFlow<UiState<String>> = _adminUiState.asStateFlow()

    private fun getToken(): String? {
        val token = sharedPreferences.getString("auth_token", null)
        return if (!token.isNullOrEmpty()) "Bearer $token" else null
    }

    /**
     * 1️⃣ جلب قائمة المؤسسات المعلقة من السيرفر
     */
    fun fetchPendingOrganizations() {
        viewModelScope.launch {
            _adminUiState.value = UiState.Loading
            val token = getToken()

            if (token == null) {
                _adminUiState.value = UiState.Error("غير مصرح: لا يوجد توكن صالح")
                return@launch
            }

            try {
                val response = apiService.getPendingOrganizations()
                if (response.isSuccessful && response.body() != null) {
                    _pendingOrganizations.value = response.body()!!
                    _adminUiState.value = UiState.Success("تم جلب البيانات بنجاح")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "فشل في جلب البيانات"
                    _adminUiState.value = UiState.Error("خطأ ${response.code()}: $errorMsg")
                }
            } catch (e: Exception) {
                Log.e("AdminViewModelApi", "Error fetching pending orgs", e)
                _adminUiState.value = UiState.Error(e.localizedMessage ?: "حدث خطأ بالشبكة")
            }
        }
    }

    /**
     * 2️⃣ الموافقة على اعتماد المؤسسة (Approve)
     */
    fun approveOrganization(orgId: Int) {
        viewModelScope.launch {
            _adminUiState.value = UiState.Loading
            val token = getToken()

            if (token == null) {
                _adminUiState.value = UiState.Error("غير مصرح: يرجى إعادة تسجيل الدخول")
                return@launch
            }

            try {
                val response = apiService.approveOrganization(orgId)
                if (response.isSuccessful) {
                    _pendingOrganizations.value = _pendingOrganizations.value.filter {
                        it.id != orgId
                    }
                    _adminUiState.value = UiState.Success("تم اعتماد المؤسسة بنجاح ✔️")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "فشل في اعتماد المؤسسة"
                    _adminUiState.value = UiState.Error("خطأ ${response.code()}: $errorMsg")
                }
            } catch (e: Exception) {
                _adminUiState.value = UiState.Error(e.localizedMessage ?: "حدث خطأ بالشبكة")
            }
        }
    }

    /**
     * 3️⃣ رفض طلب المؤسسة (Reject)
     */
    fun rejectOrganization(orgId: Int) {
        viewModelScope.launch {
            _adminUiState.value = UiState.Loading
            val token = getToken()

            if (token == null) {
                _adminUiState.value = UiState.Error("غير مصرح: يرجى إعادة تسجيل الدخول")
                return@launch
            }

            try {
                val response = apiService.rejectOrganization(orgId)
                if (response.isSuccessful) {
                    _pendingOrganizations.value = _pendingOrganizations.value.filter {
                        it.id != orgId
                    }
                    _adminUiState.value = UiState.Success("تم رفض طلب المؤسسة ❌")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "فشل في رفض الطلب"
                    _adminUiState.value = UiState.Error("خطأ ${response.code()}: $errorMsg")
                }
            } catch (e: Exception) {
                _adminUiState.value = UiState.Error(e.localizedMessage ?: "حدث خطأ بالشبكة")
            }
        }
    }

    /**
     * 4️⃣ جلب قائمة الأنشطة (Admin Activities)
     */
    fun fetchActivities() {
        viewModelScope.launch {
            _adminUiState.value = UiState.Loading
            try {
                val response = apiService.getActivities()
                if (response.isSuccessful && response.body() != null) {
                    _activities.value = response.body()!!
                    _adminUiState.value = UiState.Idle
                } else {
                    _adminUiState.value = UiState.Error("فشل في جلب الأنشطة")
                }
            } catch (e: Exception) {
                _adminUiState.value = UiState.Error(e.localizedMessage ?: "حدث خطأ بالشبكة")
            }
        }
    }

    /**
     * 5️⃣ جلب التقارير والإحصائيات
     */
    fun fetchReports() {
        viewModelScope.launch {
            _adminUiState.value = UiState.Loading
            try {
                val response = apiService.getVolunteerHoursReport()
                if (response.isSuccessful && response.body() != null) {
                    _reports.value = response.body()!!
                    _adminUiState.value = UiState.Idle
                } else {
                    _adminUiState.value = UiState.Error("فشل في جلب التقارير")
                }
            } catch (e: Exception) {
                _adminUiState.value = UiState.Error(e.localizedMessage ?: "حدث خطأ بالشبكة")
            }
        }
    }

    fun resetUiState() {
        _adminUiState.value = UiState.Idle
    }
}