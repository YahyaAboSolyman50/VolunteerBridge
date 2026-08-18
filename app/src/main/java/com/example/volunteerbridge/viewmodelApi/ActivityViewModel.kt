package com.example.volunteerbridge.viewmodelApi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.data.model.request.ActivityRequest
import com.example.volunteerbridge.data.model.response.ActivityResponse
import com.example.volunteerbridge.data.model.response.ParticipationResponse
import com.example.volunteerbridge.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivityViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    // جميع الفرص العامة (للطالب)
    private val _activities = MutableStateFlow<List<ActivityResponse>?>(emptyList())
    val activities: StateFlow<List<ActivityResponse>?> = _activities.asStateFlow()

    // الفرص الخاصة بالمؤسسة فقط (التي أضافتها)
    private val _myActivities = MutableStateFlow<List<ActivityResponse>>(emptyList())
    val myActivities: StateFlow<List<ActivityResponse>> = _myActivities.asStateFlow()

    // 🌟 قائمة معرّفات الأنشطة التي انضم إليها الطالب مسبقاً
    private val _myParticipationsIds = MutableStateFlow<Set<Int>>(emptySet())
    val myParticipationsIds: StateFlow<Set<Int>> = _myParticipationsIds.asStateFlow()

    // 🌟 قائمة مشاركات الطالب الكاملة لفحص حالاتها (مقبول، قيد الانتظار، مرفوض، مكتمل)
    private val _myParticipations = MutableStateFlow<List<ParticipationResponse>>(emptyList())
    val myParticipations: StateFlow<List<ParticipationResponse>> = _myParticipations.asStateFlow()

    private val _selectedActivity = MutableStateFlow<ActivityResponse?>(null)
    val selectedActivity: StateFlow<ActivityResponse?> = _selectedActivity.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isCreating = MutableStateFlow(false)
    val isCreating: StateFlow<Boolean> = _isCreating.asStateFlow()

    private val _joinStatus = MutableStateFlow<Boolean?>(null)
    val joinStatus: StateFlow<Boolean?> = _joinStatus.asStateFlow()

    private val _isJoinLoading = MutableStateFlow(false)
    val isJoinLoading: StateFlow<Boolean> = _isJoinLoading.asStateFlow()

    private val _selectedActivityId = MutableStateFlow<Int?>(null)
    val selectedActivityId: StateFlow<Int?> = _selectedActivityId.asStateFlow()

    fun selectActivity(activity: ActivityResponse) {
        _selectedActivity.value = activity
    }

    fun clearSelectedActivity() {
        _selectedActivity.value = null
    }

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    /**
     * 1️⃣ جلب جميع الفرص في النظام وجلب مشاركات الطالب لتحديث حالة الزر
     */
    fun loadActivities() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getActivities()

                if (response.isSuccessful) {
                    val body = response.body()
                    val listToAssign: List<ActivityResponse> = body ?: emptyList()

                    Log.d("ActivityVM", "Raw Response Body size: ${listToAssign.size}")

                    _activities.value = listToAssign
                    Log.d("ActivityVM", "تم جلب الفرص العامة: ${_activities.value?.size}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    _activities.value = emptyList()
                    Log.e("ActivityVM", "فشل جلب الفرص العامة - الكود: ${response.code()} | الرد: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("ActivityVM", "Exception in loadActivities: ${e.localizedMessage}")
                _activities.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 🌟 دالة جلب أنشطة/مشاركات الطالب للتحقق من حالتها (معلقة، مقبولة، إلخ)
     */
    fun loadMyParticipations() {
        viewModelScope.launch {
            try {
                val response = apiService.getMyParticipations()

                if (response.isSuccessful) {
                    val participations = response.body() ?: emptyList()
                    _myParticipations.value = participations

                    val ids = participations.map { it.activity }.toSet()
                    _myParticipationsIds.value = ids

                    Log.d("ActivityVM", "تم جلب المشاركات بنجاح. العدد: ${participations.size}")
                } else {
                    Log.e("ActivityVM", "فشل جلب المشاركات - الكود: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ActivityVM", "Exception in loadMyParticipations: ${e.localizedMessage}")
            }
        }
    }

    /**
     * 2️⃣ 🌟 دالة جلب فرص المؤسسة الحالية فقط
     */
    fun loadMyOrganizationActivities() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getActivitiesMyOrg()

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("ActivityVM", "نجاح جلب فرص المؤسسة. حجم القائمة: ${body?.size}")
                    _myActivities.value = body ?: emptyList()
                } else {
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string()
                    Log.e("ActivityVM", "فشل جلب فرص المؤسسة - الكود: $errorCode | الرد: $errorBody")
                    _myActivities.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("ActivityVM", "Exception in loadMyOrganizationActivities: ${e.localizedMessage}")
                _myActivities.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 3️⃣ إنشاء فرصة جديدة من قِبل المؤسسة وإعادة جلب القائمة فوراً
     */
    fun createOpportunity(request: ActivityRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isCreating.value = true
            try {
                val response = apiService.createActivity(request)

                if (response.isSuccessful) {
                    Log.d("ActivityVM", "تم إضافة الفرصة بنجاح!")
                    loadMyOrganizationActivities()
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ActivityVM", "خطأ في إنشاء الفرصة: ${response.code()} | Body: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("ActivityVM", "Exception in createOpportunity: ${e.localizedMessage}")
            } finally {
                _isCreating.value = false
            }
        }
    }

    /**
     * 4️⃣ 🌟 دالة الانضمام للفرصة (Quick Apply)
     */
    fun joinActivity(activityId: Int, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            _selectedActivityId.value = activityId
            _isJoinLoading.value = true
            try {
                val response = apiService.joinActivity(activityId)

                if (response.isSuccessful) {
                    _joinStatus.value = true
                    Log.d("ActivityVM", "تم الانضمام للفرصة بنجاح: $activityId")
                    loadScreenData()
                    onSuccess(activityId)
                } else {
                    _joinStatus.value = false
                    val errorBody = response.errorBody()?.string()
                    Log.e("ActivityVM", "فشل الانضمام للفرصة - الكود: ${response.code()} | الرد: $errorBody")
                }
            } catch (e: Exception) {
                _joinStatus.value = false
                Log.e("ActivityVM", "Exception in joinActivity: ${e.localizedMessage}")
            } finally {
                _isJoinLoading.value = false
                _selectedActivityId.value = null
            }
        }
    }

    /**
     * 5️⃣ جلب تقارير المؤسسات من Endpoint: /activities/organizations-report/
     */
    fun fetchOrganizationsReport() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getOrganizationsReport()

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("ActivityVM", "نجح جلب تقارير المؤسسات. الرد: $body")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ActivityVM", "فشل جلب تقارير المؤسسات - الكود: ${response.code()} | الرد: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("ActivityVM", "Exception in fetchOrganizationsReport: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 6️⃣ جلب تقارير ساعات المتطوعين من Endpoint: /activities/volunteer-hours-report/
     */
    fun fetchVolunteerHoursReport() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getVolunteerHoursReport()

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("ActivityVM", "نجح جلب تقرير ساعات المتطوعين. الرد: $body")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ActivityVM", "فشل جلب تقرير الساعات - الكود: ${response.code()} | الرد: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("ActivityVM", "Exception in fetchVolunteerHoursReport: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 7️⃣ جلب تفاصيل نشاط واحد عبر المعرّف (ID)
     */
    fun getActivityById(activityId: Int, onSuccess: (ActivityResponse) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getActivityById(activityId)

                if (response.isSuccessful) {
                    response.body()?.let { activity ->
                        _selectedActivity.value = activity
                        onSuccess(activity)
                        Log.d("ActivityVM", "تم جلب تفاصيل النشاط بنجاح: ${activity.title}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ActivityVM", "فشل جلب تفاصيل النشاط - الكود: ${response.code()} | الرد: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("ActivityVM", "Exception in getActivityById: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 8️⃣ تعديل فرصة قائمة (Update Activity) وإعادة جلب قائمة فرص المؤسسة فوراً
     */
    fun updateActivity(
        activityId: Int,
        request: ActivityRequest,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isUpdating.value = true
            try {
                val response = apiService.partialUpdateActivity(activityId, request)

                if (response.isSuccessful) {
                    Log.d("ActivityVM", "تم تعديل الفرصة بنجاح!")
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ActivityVM", "فشل تعديل الفرصة - الكود: ${response.code()} | الرد: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("ActivityVM", "Exception in updateActivity: ${e.localizedMessage}")
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun loadScreenData(onFinished: () -> Unit = {}) {
        viewModelScope.launch {
            loadActivities()
            loadMyParticipations()
            onFinished()
        }
    }

    fun formatToken(token: String): String {
        val cleanToken = token.trim()
        return if (cleanToken.startsWith("Bearer ", ignoreCase = true)) {
            cleanToken
        } else {
            "Bearer $cleanToken"
        }
    }

    /**
     * 9️⃣ جلب قائمة المشاركات/المتطوعين المرتبطين بفرصة محددة (عبر الـ Activity ID)
     */
    fun loadActivityApplications(activityId: Int, onResult: (List<ParticipationResponse>) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getActivityApplications(activityId)

                if (response.isSuccessful) {
                    val applications = response.body() ?: emptyList()
                    Log.d("ActivityVM", "تم جلب طلبات الفرصة بنجاح. العدد: ${applications.size}")
                    onResult(applications)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ActivityVM", "فشل جلب طلبات الفرصة - الكود: ${response.code()} | الرد: $errorBody")
                    onResult(emptyList())
                }
            } catch (e: Exception) {
                Log.e("ActivityVM", "Exception in loadActivityApplications: ${e.localizedMessage}")
                onResult(emptyList())
            } finally {
                _isLoading.value = false
            }
        }
    }
}