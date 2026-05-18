package com.example.volunteerbridge.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.model.classes.NotificationModel
import com.example.volunteerbridge.model.classes.OpportunityModel
import com.example.volunteerbridge.model.classes.status.UiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OpportunityViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<UiState<List<OpportunityModel>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<OpportunityModel>>> = _uiState.asStateFlow()

    // --- الجزء الخاص بالطالب ---
    private val _allOppForStudent = mutableStateOf<List<OpportunityModel>>(emptyList())
    private val _filteredOppForStudent = mutableStateOf<List<OpportunityModel>>(emptyList())
    val filteredOppForStudent = _filteredOppForStudent

    // --- الجزء الخاص بالمؤسسة ---
    private val _orgOpp = mutableStateOf<List<OpportunityModel>>(emptyList())
    val orgOpp = _orgOpp

    var selectedStatus by mutableStateOf("All")
    var selectedOrgType by mutableStateOf("All")
    var selectedCategory by mutableStateOf("All")

    /**
     * دالة رفع الفرصة إلى Firebase Firestore مع تصفير الحالة تلقائياً
     */
    fun uploadOpportunity(oppModel: OpportunityModel) {
        _uiState.value = UiState.Loading
        val docRef = db.collection("opportunities").document()
        val finalOpp = oppModel.copy(id = docRef.id)

        docRef.set(finalOpp)
            .addOnSuccessListener {
                _uiState.value = UiState.Success(listOf(finalOpp))
                // ✨ تصفير الحالة تلقائياً بعد نجاح الرفع
                viewModelScope.launch {
                    delay(500) // تأخير نصف ثانية ليلاحظ المستخدم إتمام العملية بنجاح
                    resetState()
                }
            }
            .addOnFailureListener { e ->
                _uiState.value = UiState.Error(getFriendlyMessage(e))
            }
    }

    /**
     * 1. دالة الطالب: تجلب كل الفرص
     */
    fun fetchAllForStudent() {
        _uiState.value = UiState.Loading
        db.collection("opportunities")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _uiState.value = UiState.Error(getFriendlyMessage(e))
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(OpportunityModel::class.java) ?: emptyList()
                _allOppForStudent.value = list
                _filteredOppForStudent.value = list
                _uiState.value = UiState.Success(list)
            }
    }

    /**
     * 2. دالة فلترة الطالب (محلياً)
     */
    fun filterStudentData(query: String) {
        _filteredOppForStudent.value = _allOppForStudent.value.filter { opp ->
            val matchesSearch = opp.title.contains(query, ignoreCase = true) ||
                    opp.tags.any { it.contains(query, ignoreCase = true) }

            val matchesStatus = if (selectedStatus == "All") true else opp.status == selectedStatus
            val matchesOrgType = if (selectedOrgType == "All") true else opp.orgType == selectedOrgType
            val matchesCategory = if (selectedCategory == "All") true else opp.category == selectedCategory

            matchesSearch && matchesStatus && matchesOrgType && matchesCategory
        }
    }

    /**
     * 3. دالة المؤسسة: تجلب بياناتها وتتحقق من انتهاء الصلاحية الذكي
     */
    fun fetchOrgData(orgId: String) {
        _uiState.value = UiState.Loading

        db.collection("opportunities")
            .whereEqualTo("orgId", orgId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _uiState.value = UiState.Error(getFriendlyMessage(e))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        val obj = doc.toObject(OpportunityModel::class.java)
                        obj?.copy(id = doc.id)
                    }

                    val currentTime = System.currentTimeMillis()

                    val updatedList = list.map { opp ->
                        val isExpired = (opp.deadline in 1 until currentTime) ||
                                (opp.endDate in 1 until currentTime)

                        if (isExpired && opp.status == "Active") {
                            viewModelScope.launch {
                                updateOpportunityStatusWithNotification(opp.id, orgId, opp.title, "Closed")
                            }
                            opp.copy(status = "Closed")
                        } else {
                            opp
                        }
                    }

                    _orgOpp.value = updatedList
                    _uiState.value = UiState.Success(updatedList)
                }
            }
    }

    /**
     * دالة تحديث بيانات الفرصة مع تصفير الحالة تلقائياً
     */
    fun updateOpportunity(oppId: String, updatedData: Map<String, Any>) {
        _uiState.value = UiState.Loading
        db.collection("opportunities").document(oppId)
            .update(updatedData)
            .addOnSuccessListener {
                _uiState.value = UiState.Success(emptyList())
                // ✨ تصفير الحالة تلقائياً بعد نجاح التعديل وحفظ التغييرات
                viewModelScope.launch {
                    delay(500)
                    resetState()
                }
            }
            .addOnFailureListener { e ->
                _uiState.value = UiState.Error(getFriendlyMessage(e))
            }
    }

    private suspend fun updateOpportunityStatusWithNotification(
        oppId: String,
        orgId: String,
        oppTitle: String,
        newStatus: String = "Closed"
    ) {
        try {
            db.collection("opportunities").document(oppId)
                .update("status", newStatus)
                .await()

            val notDocRef = db.collection("notifications").document()
            val notification = NotificationModel(
                notificationId = notDocRef.id,
                receiverId = orgId,
                title = "Opportunity Expired",
                message = "Your opportunity '$oppTitle' has been automatically closed due to deadline/end date.",
                timestamp = System.currentTimeMillis(),
                type = "CLOSED",
                isRead = false
            )
            notDocRef.set(notification).await()
        } catch (e: Exception) {
            Log.e("UpdateStatus", "Failed to update status: ${e.message}")
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

    fun resetState() { _uiState.value = UiState.Idle }

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