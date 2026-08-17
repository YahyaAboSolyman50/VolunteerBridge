package com.example.volunteerbridge.viewmodelApi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.data.model.response.ParticipationResponse
import com.example.volunteerbridge.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * الـ ViewModel الجديد الخاص بطلبات انضمام الطالب للفرص عبر الـ API
 */
class ParticipationViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    // استبدال كلاس Firebase بكلاس الـ Response الفعلي للسيرفر
    private val _applications = MutableStateFlow<List<ParticipationResponse>>(emptyList())
    val applications: StateFlow<List<ParticipationResponse>> = _applications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * جلب طلبات التقديم والمشاركات الخاصة بالطالب الحالي عبر الـ API
     */
    fun fetchStudentApplications() {


        viewModelScope.launch {
            _isLoading.value = true
            try {

                // 🟢 استدعاء الـ Endpoint: activities/my-participations/
                val response = apiService.getMyParticipations()

                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!

                    // الترتيب التنازلي حسب تاريخ الانضمام القادم من السيرفر (joined_at)
                    _applications.value = list.sortedByDescending { it.joinedAt }
                    Log.d("ApplicationsViewModel", "تم جلب ${list.size} طلب بنجاح.")
                } else {
                    Log.e("ApplicationsViewModel", "فشل جلب الطلبات. كود الخطأ: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(
                    "ApplicationsViewModel",
                    "حدث خطأ أثناء الاتصال بالسيرفر: ${e.localizedMessage}"
                )
            } finally {
                _isLoading.value = false
            }
        }
        /**
         * إلغاء طلب التقديم (ملاحظة: السيرفر لا يحتوي على Delete حالياً للطلب،
         * ولكن يمكنك هنا مستقبلاً ربطه بـ Endpoint الإلغاء إذا تم تحديثه)
         */
    }

    fun cancelApplication(appId: Int,onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                apiService.cancelParticipation(appId)
                onResult(true)
            } catch (e: Exception) {
                Log.e("ApplicationsViewModel", "خطأ في إلغاء الطلب: ${e.message}")
                onResult(false)
            }
        }
    }

    /**
     * 🟢 جلب جميع اشتراكات وطلبات الانضمام الخاصة بفرص المؤسسة الحالية
     */
    fun fetchOrganizationApplications() {


        viewModelScope.launch {
            _isLoading.value = true
            try {

                // استدعاء Endpoint الخاصة بطلبات المؤسسة
                val response = apiService.getMyApplications()

                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!
                    _applications.value = list.sortedByDescending { it.joinedAt }
                    Log.d("ApplicationsViewModel", "تم جلب ${list.size} طلب للمؤسسة بنجاح.")
                } else {
                    Log.e("ApplicationsViewModel", "فشل جلب طلبات المؤسسة. كود الخطأ: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(
                    "ApplicationsViewModel",
                    "حدث خطأ أثناء الاتصال بالسيرفر لجلب طلبات المؤسسة: ${e.localizedMessage}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approveApplication( participationId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.approveParticipation( participationId = participationId)
                if (response.isSuccessful) {
                    // تحديث القائمة محلياً لتغيير حالة الطلب إلى Accepted
                    _applications.value = _applications.value.map {
                        if (it.id == participationId) it.copy(status = "Approved") else it
                    }
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("ApplicationsViewModel", "خطأ في قبول الطلب: ${e.message}")
                onResult(false)
            }
        }
    }

    fun rejectApplication(participationId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.rejectParticipation( participationId = participationId)
                if (response.isSuccessful) {
                    // تحديث القائمة محلياً لتغيير حالة الطلب إلى Rejected
                    _applications.value = _applications.value.map {
                        if (it.id == participationId) it.copy(status = "Rejected") else it
                    }
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("ApplicationsViewModel", "خطأ في رفض الطلب: ${e.message}")
                onResult(false)
            }
        }
    }

    /**
     * 🟢 جلب طلبات المتقدمين الخاصة بفرصة معينة للمؤسسة عبر معرف الفرصة (activityId)
     */
    fun fetchApplicantsForActivity(activityId: Int) {


        viewModelScope.launch {
            _isLoading.value = true
            try {

                // استدعاء الـ Endpoint: /activities/{id}/applications/
                val response = apiService.getActivityApplications( activityId = activityId)

                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!
                    _applications.value = list.sortedByDescending { it.joinedAt }
                    Log.d("ApplicationsViewModel", "تم جلب ${list.size} متقدم للفرصة رقم $activityId بنجاح.")
                } else {
                    Log.e("ApplicationsViewModel", "فشل جلب المتقدمين للفرصة. كود الخطأ: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(
                    "ApplicationsViewModel",
                    "حدث خطأ أثناء الاتصال بالسيرفر لجلب متقدمي الفرصة: ${e.localizedMessage}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun completeParticipation(participationId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.completeParticipation(participationId)
                if (response.isSuccessful) {
                    onResult(true)
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("API_ERROR", "Complete failed with code ${response.code()}: $errorBody")
                    onResult(false)
                }
            } catch (e: Exception) {
                android.util.Log.e("API_EXCEPTION", "Exception: ${e.message}")
                onResult(false)
            }
        }
    }
}