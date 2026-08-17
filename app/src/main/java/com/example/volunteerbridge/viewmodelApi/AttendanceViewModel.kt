package com.example.volunteerbridge.viewmodelApi

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.data.model.request.DailyActivityLogRequest
import com.example.volunteerbridge.data.model.request.VolunteerAttendanceRequest
import com.example.volunteerbridge.data.model.response.DailyActivityLogResponse
import com.example.volunteerbridge.data.model.response.GenericReportResponse
import com.example.volunteerbridge.data.model.response.TotalHoursResponse
import com.example.volunteerbridge.data.model.response.VolunteerAttendanceResponse
import com.example.volunteerbridge.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    private val _attendanceList = MutableStateFlow<List<VolunteerAttendanceResponse>>(emptyList())
    val attendanceList: StateFlow<List<VolunteerAttendanceResponse>> = _attendanceList

    private val _dailyLogs = MutableStateFlow<List<DailyActivityLogResponse>>(emptyList())
    val dailyLogs: StateFlow<List<DailyActivityLogResponse>> = _dailyLogs

    // الحالات الخاصة بإجمالي الساعات والتقارير بناءً على نماذج الـ API الصحيحة
    private val _totalHours = MutableStateFlow<TotalHoursResponse?>(null)
    val totalHours: StateFlow<TotalHoursResponse?> = _totalHours

    private val _attendanceReport = MutableStateFlow<GenericReportResponse?>(null)
    val attendanceReport: StateFlow<GenericReportResponse?> = _attendanceReport

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    /**
     * جلب سجل الحضور والغياب الخاص بالطالب
     */
    fun fetchAttendance() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getActivitiesAttendanceList()
                if (response.isSuccessful && response.body() != null) {
                    _attendanceList.value = response.body()!!
                }
            } catch (e: Exception) {
                _attendanceList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * تسجيل عملية حضور جديدة (مسح كود الحضور مثلاً)
     */
    fun checkInVolunteer(request: VolunteerAttendanceRequest, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.createActivitiesAttendance(request)
                if (response.isSuccessful) {
                    onComplete(true)
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("AttendanceError", "Failed: $errorBody")
                    onComplete(false)
                }
            } catch (e: Exception) {
                android.util.Log.e("AttendanceError", "Exception: ${e.message}")
                onComplete(false)
            }
        }
    }

    /**
     * جلب اليوميات والتقارير المكتوبة
     */
    fun fetchDailyLogs() {
        viewModelScope.launch {
            try {
                val response = apiService.getDailyLogsList()
                if (response.isSuccessful && response.body() != null) {
                    _dailyLogs.value = response.body()!!
                }
            } catch (e: Exception) {
                _dailyLogs.value = emptyList()
            }
        }
    }

    /**
     * إنشاء وحفظ تقرير يومي جديد لتقديمه للمشرف
     */
    fun submitDailyLog(logRequest: DailyActivityLogRequest, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.createDailyLog(logRequest)
                if (response.isSuccessful) {
                    fetchDailyLogs() // إعادة تحديث القائمة فوراً
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    // ==========================================
    // الدوال المرتبطة بالـ Attendance والساعات (المطابقة لملف الـ ApiService)
    // ==========================================

    /**
     * جلب إجمالي ساعات التطوع الخاصة بالمستخدم (Endpoints: /activities/my-total-hours/)
     */
    fun fetchMyTotalHours() {
        viewModelScope.launch {
            try {
                val response = apiService.getMyTotalHours()
                if (response.isSuccessful && response.body() != null) {
                    _totalHours.value = response.body()
                }
            } catch (e: Exception) {
                // التعامل مع الخطأ
            }
        }
    }

    /**
     * جلب تقرير ساعات الحضور الخاصة بالمتطوعين (Endpoints: /activities/volunteer-hours-report/)
     */
    fun fetchVolunteerHoursReport() {
        viewModelScope.launch {
            try {
                val response = apiService.getVolunteerHoursReport()
                if (response.isSuccessful && response.body() != null) {
                    _attendanceReport.value = response.body()
                }
            } catch (e: Exception) {
                // التعامل مع الخطأ
            }
        }
    }
}