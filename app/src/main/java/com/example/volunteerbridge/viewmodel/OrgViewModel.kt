package com.example.volunteerbridge.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.model.classes.ApplicationModel
import com.example.volunteerbridge.model.classes.NotificationModel
import com.example.volunteerbridge.model.classes.Organization
import com.example.volunteerbridge.model.classes.VolunteerTask
import com.example.volunteerbridge.model.classes.status.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrgViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()

    // ✨ الـ UiState الخاص بالوظائف العامة للمؤسسة (الطلبات، التوثيق)
    private val _orgUiState = MutableStateFlow<UiState<List<ApplicationModel>>>(UiState.Idle)
    val orgUiState: StateFlow<UiState<List<ApplicationModel>>> = _orgUiState.asStateFlow()

    // 📧 ✨ فصل الـ UiState الخاص بـ تفعيل البريد الإلكتروني تماماً لمنع التداخل
    private val _emailUiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val emailUiState: StateFlow<UiState<Unit>> = _emailUiState.asStateFlow()

    // كائن البيانات الخاص بالمؤسسة للشاشات التابعة لها
    val currentOrgData = mutableStateOf<Organization?>(null)

    private val _orgApplications = mutableStateOf<List<ApplicationModel>>(emptyList())
    val orgApplications = _orgApplications

    // تحويل المرجع ليكون مراقباً بشكل صحيح وآمن في الكومبوز
    private val _isEmailVerified = mutableStateOf(false)
    val isEmailVerified: State<Boolean> = _isEmailVerified

    /**
     * 📧 دالة إرسال رابط تفعيل البريد الإلكتروني
     */
    fun sendVerificationEmail() {
        val currentUser = auth.currentUser ?: return
        _emailUiState.value = UiState.Loading

        currentUser.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("OrgViewModel", "✔️ sendVerificationEmail: Success")
                    _emailUiState.value = UiState.Success(Unit)
                } else {
                    val errMsg = task.exception?.localizedMessage ?: "فشل إرسال البريد الإلكتروني"
                    _emailUiState.value = UiState.Error(errMsg)
                }
            }
    }

    /**
     * 🔄 دالة فحص حالة التفعيل حياً من السيرفر (تحدث صامتاً خلف الكواليس)
     */
    fun checkEmailVerificationStatus() {
        val currentUser = auth.currentUser ?: return

        currentUser.reload().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _isEmailVerified.value = currentUser.isEmailVerified
                Log.d("OrgViewModel", "حالة الإيميل المحدثة: ${currentUser.isEmailVerified}")

                if (currentUser.isEmailVerified) {
                    _emailUiState.value = UiState.Success(Unit)
                }
            } else {
                Log.e("OrgViewModel", "خطأ في الاتصال أثناء تحديث الإيميل")
                // لا نغير حالة الـ UiState إلى Error هنا لكي لا تظهر رسالة خطأ مزعجة للمستخدم كل 3 ثوانٍ بسبب ضعف شبكة عابر
            }
        }
    }

    fun resetEmailUiState() {
        _emailUiState.value = UiState.Idle
    }

    fun resetUiState() {
        _orgUiState.value = UiState.Idle
    }

    /**
     * دالة جلب بيانات المؤسسة الحالية (تُستدعى في الشاشات الخاصة بالمؤسسة)
     */
    fun fetchCurrentOrgProfile() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val doc = db.collection("organizations").document(uid).get().await()
                if (doc.exists()) {
                    currentOrgData.value = doc.toObject(Organization::class.java)
                }
            } catch (e: Exception) {
                Log.e("OrgViewModel", "Error fetching profile: ${e.message}")
            }
        }
    }

    /**
     * 1️⃣ دالة المؤسسة: تقديم طلب التوثيق للأدمن (مبنية على UiState)
     */
    fun submitVerificationRequest() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _orgUiState.value = UiState.Loading
            try {
                val updateData = mapOf(
                    "status" to "Pending",
                    "verified" to false
                )

                db.collection("organizations")
                    .document(uid)
                    .update(updateData)
                    .await()

                fetchCurrentOrgProfile()
                _orgUiState.value = UiState.Success(emptyList())
            } catch (e: Exception) {
                Log.e("OrgViewModel", "Failed to submit request: ${e.message}")
                _orgUiState.value = UiState.Error(
                    e.localizedMessage ?: "حدث خطأ أثناء إرسال طلب التوثيق"
                )
            }
        }
    }

    /**
     * 2️⃣ دالة الأدمن: الموافقة على توثيق المؤسسة (مبنية على UiState)
     */
    fun adminApproveOrganization(orgUid: String) {
        viewModelScope.launch {
            _orgUiState.value = UiState.Loading
            try {
                val updateData = mapOf(
                    "status" to "Approved",
                    "verified" to true
                )

                db.collection("organizations")
                    .document(orgUid)
                    .update(updateData)
                    .await()

                _orgUiState.value = UiState.Success(emptyList())
            } catch (e: Exception) {
                Log.e("OrgViewModel", "Admin approval failed: ${e.message}")
                _orgUiState.value = UiState.Error(
                    e.localizedMessage ?: "حدث خطأ أثناء اعتماد المؤسسة من الأدمن"
                )
            }
        }
    }

    /**
     * دالة التحديث التي نقلناها من الـ Auth
     */
    fun updateOrganizationData(updatedOrg: Organization) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                db.collection("organizations").document(uid)
                    .set(updatedOrg)
                    .await()

                currentOrgData.value = updatedOrg
                Log.d("OrgViewModel", "Org data updated successfully!")
            } catch (e: Exception) {
                Log.e("OrgViewModel", "Error updating org: ${e.message}")
            }
        }
    }

    /**
     * جلب طلبات التقديم الخاصة بالمؤسسة مع تحديث الـ UiState
     */
    fun fetchApplicationsForOrg(orgId: String) {
        _orgUiState.value = UiState.Loading

        db.collection("applications")
            .whereEqualTo("orgId", orgId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _orgUiState.value = UiState.Error(e.localizedMessage ?: "Failed to load applications")
                    return@addSnapshotListener
                }

                val list = snapshot?.toObjects(ApplicationModel::class.java) ?: emptyList()
                val sortedList = list.sortedByDescending { it.appliedAt }

                _orgApplications.value = sortedList
                _orgUiState.value = UiState.Success(sortedList)
            }
    }

    /**
     * قبول أو رفض طلب التقديم وإرسال إشعارات فورية للطالب
     */
    fun updateApplicationStatus(appId: String, newStatus: String, studentId: String, oppTitle: String) {
        viewModelScope.launch {
            try {
                db.collection("applications").document(appId)
                    .update("status", newStatus)
                    .await()

                val notDocRef = db.collection("notifications").document()
                val notification = NotificationModel(
                    notificationId = notDocRef.id,
                    receiverId = studentId,
                    title = if (newStatus == "Accepted") "Congratulations! 🎉" else "Application Update",
                    message = "Your application for '$oppTitle' has been $newStatus.",
                    timestamp = System.currentTimeMillis(),
                    type = if (newStatus == "Accepted") "ACCEPTED" else "REJECTED",
                    isRead = false
                )
                notDocRef.set(notification).await()
            } catch (e: Exception) {
                Log.e("UpdateStatus", "Error: ${e.message}")
            }
        }
    }

    fun assignTaskToStudent(
        oppId: String, oppTitle: String, studentId: String,
        title: String, description: String, dueDate: Long
    ) {
        viewModelScope.launch {
            try {
                val taskDocRef = db.collection("tasks").document()
                val newTask = VolunteerTask(
                    taskId = taskDocRef.id, oppId = oppId, oppTitle = oppTitle,
                    studentId = studentId, title = title, description = description,
                    dueDate = dueDate, status = "Pending"
                )
                taskDocRef.set(newTask).await()
            } catch (e: Exception) {
                Log.e("OrgViewModel", "Error assigning task: ${e.message}")
            }
        }
    }

    fun finalizeAndCreditStudent(appId: String, studentId: String, requiredHours: Int, oppTitle: String) {
        viewModelScope.launch {
            try {
                db.collection("applications").document(appId).update("status", "Completed").await()

                val studentDocRef = db.collection("student").document(studentId)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(studentDocRef)
                    val currentHours = snapshot.getLong("totalEarnedHours") ?: 0L
                    transaction.update(studentDocRef, "totalEarnedHours", currentHours + requiredHours)
                }.await()

                val notDocRef = db.collection("notifications").document()
                val notification = NotificationModel(
                    notificationId = notDocRef.id, receiverId = studentId,
                    title = "🎉 Volunteering Completed!",
                    message = "Congratulations! You completed '$oppTitle' and earned $requiredHours hours.",
                    timestamp = System.currentTimeMillis(), type = "COMPLETED", isRead = false
                )
                notDocRef.set(notification).await()
            } catch (e: Exception) {
                Log.e("OrgViewModel", "Error finalizing volunteer: ${e.message}")
            }
        }
    }
}