package com.example.volunteerbridge.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.model.classes.ApplicationModel
import com.example.volunteerbridge.model.classes.NotificationModel
import com.example.volunteerbridge.model.classes.OpportunityModel
import com.example.volunteerbridge.model.classes.UserModel // تأكد من مسار الـ UserModel الخاص بالطالب
import com.example.volunteerbridge.model.classes.status.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StudentViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<UiState<List<OpportunityModel>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<OpportunityModel>>> = _uiState.asStateFlow()

    // كائن بيانات الطالب الموحد للشاشات
    val currentUserData = mutableStateOf<UserModel?>(null)
    private val _appliedOppIds = mutableStateOf<Set<String>>(emptySet())
    val appliedOppIds = _appliedOppIds

    /**
     * دالة جلب بيانات الملف الشخصي للطالب الحالية
     */
    fun fetchCurrentStudentProfile() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val doc = db.collection("student").document(uid).get().await()
                if (doc.exists()) {
                    currentUserData.value = doc.toObject(UserModel::class.java)
                    Log.d("StudentViewModel", "Student data loaded successfully!")
                }
            } catch (e: Exception) {
                Log.e("StudentViewModel", "Error fetching student profile: ${e.message}")
            }
        }
    }


    fun appRegister(app: ApplicationModel) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // 1. إنشاء طلب التقديم بمعرف فريد من الفايرستور
                val appDocRef = db.collection("applications").document()
                val finalApp = app.copy(appId = appDocRef.id)
                appDocRef.set(finalApp).await()

                // 2. زيادة عدد المتقدمين للفرصة
                db.collection("opportunities").document(app.oppId)
                    .update("applicantsCount", com.google.firebase.firestore.FieldValue.increment(1))
                    .await()

                // 3. 🔥 إنشاء الإشعار مع تحديد الحقول بالاسم لضمان عدم تداخل الترتيب
                val notDocRef = db.collection("notifications").document()
                val notification = NotificationModel(
                    notificationId = notDocRef.id,
                    receiverId = app.orgId,      // تأكد أن هذا الحقل يحمل الـ UID الخاص بالمؤسسة
                    senderId = app.studentId,    // الـ UID الخاص بالطالب
                    title = "New Applicant! 📝",
                    message = "${app.studentName} applied for '${app.oppTitle}'",
                    timestamp = System.currentTimeMillis(),
                    type = "APPLICATION",
                    isRead = false
                )
                notDocRef.set(notification).await()

                // 4. ✨ تحديث الحالة المحلية فوراً ليتحول الزر إلى Applied بدون انتظار الريفرش
                val updatedIds = _appliedOppIds.value.toMutableSet()
                updatedIds.add(app.oppId)
                _appliedOppIds.value = updatedIds

                _uiState.value = UiState.Success(emptyList())
            } catch (e: Exception) {
                _uiState.value = UiState.Error(getFriendlyMessage(e))
                Log.e("AppRegister", "Error creating application or notification: ${e.message}")
            }
        }
    }

    fun fetchUserApplications(studentId: String) {
        db.collection("applications")
            .whereEqualTo("studentId", studentId)
            .addSnapshotListener { snapshot, _ ->
                val ids = snapshot?.documents?.mapNotNull { it.getString("oppId") }?.toSet() ?: emptySet()
                _appliedOppIds.value = ids
            }
    }
    fun getFriendlyMessage(exception: Exception?): String {
        return when (exception) {
            is com.google.firebase.firestore.FirebaseFirestoreException -> {
                when (exception.code) {
                    com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED -> "You don't have permission to perform this action."
                    com.google.firebase.firestore.FirebaseFirestoreException.Code.UNAVAILABLE -> "Server is temporarily unavailable. Check your internet."
                    else -> "A database error occurred. Please try again."
                }
            }
            is com.google.firebase.FirebaseNetworkException -> "No internet connection. Please check your network."
            else -> exception?.localizedMessage ?: "An unexpected error occurred."
        }
    }
    fun getDetailedStatus(opp: OpportunityModel): String {
        val currentTime = System.currentTimeMillis()
        return when {
            opp.status == "Closed" -> {
                if (opp.vacancies > 0 && opp.applicantsCount >= opp.vacancies) "Closed (Full)"
                else "Closed"
            }
            (opp.endDate in 1 until currentTime) -> "Closed (Ended)"
            (opp.deadline in 1 until currentTime) -> "Closed (Deadline)"
            (opp.vacancies > 0 && opp.applicantsCount >= opp.vacancies) -> "Closed (Full)"
            else -> "Active"
        }
    }
}