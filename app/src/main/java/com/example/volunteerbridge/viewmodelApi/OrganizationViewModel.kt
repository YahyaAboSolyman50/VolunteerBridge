package com.example.volunteerbridge.viewmodelApi

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.data.model.request.ActivityRequest
import com.example.volunteerbridge.data.model.request.OrganizationRequest
import com.example.volunteerbridge.data.model.request.VolunteerAttendanceRequest
import com.example.volunteerbridge.data.model.response.OrganizationResponse
import com.example.volunteerbridge.data.model.response.ParticipationResponse
import com.example.volunteerbridge.model.AuthValidator
import com.example.volunteerbridge.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrganizationViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    private val _pendingOrganizations = MutableStateFlow<List<OrganizationResponse>>(emptyList())
    val pendingOrganizations: StateFlow<List<OrganizationResponse>> = _pendingOrganizations

    private val _organizationsList = MutableStateFlow<List<OrganizationResponse>>(emptyList())
    val organizationsList: StateFlow<List<OrganizationResponse>> = _organizationsList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading


    // 🏢 المؤسسة المحددة حالياً أو البروفايل الخاص بالحساب الحالي
    private val _currentOrganization = mutableStateOf<OrganizationResponse?>(null)
    val currentOrganization: State<OrganizationResponse?> = _currentOrganization

    private val _orgApplications = mutableStateOf<List<ParticipationResponse>>(emptyList())
    val orgApplications: State<List<ParticipationResponse>> = _orgApplications

    // دالة مساعدة لتنسيق التوكن بأمان
    private fun formatToken(token: String): String {
        return if (token.startsWith("Bearer ")) token else "Bearer $token"
    }

    /**
     * 🏢 جلب بروفايل المؤسسة المسجلة حالياً من الـ API (تستخدم عند الدخول كـ Organization)
     */
    fun fetchCurrentOrgProfile(orgId: Int) {

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response =
                    apiService.getOrganizationById(orgId) // أو استخدام الـ endpoint المخصص إذا كان بدون id
                if (response.isSuccessful && response.body() != null) {
                    _currentOrganization.value = response.body()
                    Log.d("OrganizationViewModel", "Org Profile loaded successfully!")
                }
            } catch (e: Exception) {
                Log.e("OrganizationViewModel", "Error fetching Org Profile: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * جلب كشف حضور ومشاركات المتطوعين
     */
    fun loadOrganizationApplications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getMyParticipations()
                if (response.isSuccessful && response.body() != null) {
                    _orgApplications.value = response.body()!!
                }
            } catch (e: Exception) {
                _orgApplications.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * إنشاء نشاط/مهمة جديدة للمتطوع
     */
    fun assignTaskToStudent(
        activityRequest: ActivityRequest
    ) {
        viewModelScope.launch {
            try {
                apiService.createActivity(activityRequest)
            } catch (e: Exception) {
                Log.e("OrganizationViewModel", "Error assigning task: ${e.message}")
            }
        }
    }


    /**
     * تسجيل مؤسسة جديدة
     */
    fun registerOrganization(
        org: OrganizationRequest,
        confPassword: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val validationErrors = AuthValidator.validateSignupErrors(org, confPassword)
        if (validationErrors.hasError()) {
            onResult(false, "Please check validation errors")
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.registerOrganization(org)
                if (response.isSuccessful && response.body() != null) {
                    onResult(true, "Organization registered successfully!")
                } else {
                    onResult(false, "Failed to register organization: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "An unexpected error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * جلب كافة المؤسسات المعتمدة
     */
    fun loadOrganizations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getOrganizationsList()
                if (response.isSuccessful && response.body() != null) {
                    _organizationsList.value = response.body()!!
                }
            } catch (e: Exception) {
                _organizationsList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * جلب المؤسسات المعلقة (للأدمن)
     */
    fun loadPendingOrganizations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getPendingOrganizations()
                if (response.isSuccessful && response.body() != null) {
                    _pendingOrganizations.value = response.body()!!
                }
            } catch (e: Exception) {
                _pendingOrganizations.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * قبول طلب تسجيل المؤسسة (للأدمن)
     */
    fun approveOrganization(id: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.approveOrganization(id)
                if (response.isSuccessful) {
                    loadPendingOrganizations()
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    /**
     * رفض طلب تسجيل المؤسسة (للأدمن)
     */
    fun rejectOrganization(id: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.rejectOrganization(id)
                if (response.isSuccessful) {
                    loadPendingOrganizations()
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    /**
     * جلب بيانات مؤسسة بعينها بناءً على الـ ID
     */
    fun fetchOrganizationById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getOrganizationById(id)
                if (response.isSuccessful && response.body() != null) {
                    _currentOrganization.value = response.body()
                } else {
                    _currentOrganization.value = null
                }
            } catch (e: Exception) {
                _currentOrganization.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
    // داخل OrganizationViewModel.kt

    fun fetchMyOrganization() {
        viewModelScope.launch {
            try {
                val response = apiService.getOrganizationProfile()

                if (response.isSuccessful && response.body() != null) {
                    _currentOrganization.value = response.body()

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("OrgVM", "Server Error Message: $errorBody")
                    Log.e("OrgVM", "فشل جلب بيانات المؤسسة: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("OrgVM", "Exception in fetchMyOrganization: ${e.localizedMessage}")
            }
        }
    }

    fun updateOrganization(
        id: Int,
        org: OrganizationRequest,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = apiService.partialUpdateOrganization(id, org)

                if (response.isSuccessful && response.body() != null) {
                    Log.e("OrgVM", "Update Success")
                    fetchMyOrganization()
                    onSuccess()

                } else {
                    // تم إزالة errorBody()?.string() لتجنب مشكلة الإغلاق وتضارب القراءة
                    Log.e("OrgVM", "فشل تحديث بيانات المؤسسة: رمز الخطأ ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("OrgVM_Error", "Exception in updateOrganization: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }
}