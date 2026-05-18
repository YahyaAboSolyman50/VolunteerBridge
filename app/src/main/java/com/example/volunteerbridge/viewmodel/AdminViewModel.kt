package com.example.volunteerbridge.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.model.classes.Organization
import com.example.volunteerbridge.model.classes.status.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // ⏳ إدارة حالة عمليات الأدمن (تحميل، نجاح، خطأ) عند الموافقة على التوثيق
    private val _adminUiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val adminUiState: StateFlow<UiState<Unit>> = _adminUiState.asStateFlow()

    // قائمة المؤسسات التي تنتظر التوثيق ليراها الأدمن في شاشته
    private val _pendingOrganizations = mutableStateOf<List<Organization>>(emptyList())
    val pendingOrganizations: State<List<Organization>> = _pendingOrganizations

    // 🛡️ متغير لحفظ كائن الاستماع لكي نتمكن من إغلاقه لاحقاً وتجنب الـ Memory Leak
    private var pendingOrgsListener: ListenerRegistration? = null

    /**
     * 👀 جلب كافة المؤسسات المنتظرة للتوثيق تلقائياً وبشكل حي (Realtime) للأدمن مع خطة دفاعية
     */
    fun fetchPendingOrganizationsForAdmin() {
        // نتحقق أولاً لمنع إنشاء استماع مكرر
        if (pendingOrgsListener != null) return

        pendingOrgsListener = db.collection("organizations")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("AdminViewModel", "Admin fetch failed: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    try {
                        // 1. المحاولة القياسية: تحويل الكائنات وتصفيتها برمجياً
                        val allOrgs = snapshot.toObjects(Organization::class.java)
                        val pendingList = allOrgs.filter {
                            it.status.equals("Pending", ignoreCase = true) || it.status.equals("pending", ignoreCase = true)
                        }
                        _pendingOrganizations.value = pendingList
                        Log.d("AdminViewModel", "Realtime fetch success. Pending count: ${pendingList.size}")
                    } catch (mappingError: Exception) {
                        Log.e("AdminViewModel", "Mapping failed, using defensive fallback: ${mappingError.message}")

                        // 2. 💡 خطة دفاعية بديلة: قراءة الحقول يدوياً لحماية الأدمن من الانهيار إذا تباينت حقول السيرفر
                        val alternativeList = mutableListOf<Organization>()
                        for (doc in snapshot.documents) {
                            val statusStr = doc.getString("status") ?: "Pending"
                            if (statusStr.equals("Pending", ignoreCase = true) || statusStr.equals("pending", ignoreCase = true)) {
                                alternativeList.add(
                                    Organization(
                                        uid = doc.id,
                                        nameOrg = doc.getString("nameOrg") ?: doc.getString("name") ?: "مؤسسة بدون اسم",
                                        emailOrg = doc.getString("emailOrg") ?: "",
                                        status = statusStr,
                                        verified = doc.getBoolean("verified") ?: false,
                                        orgType = doc.getString("orgType") ?: "مؤسسة",
                                        license = doc.getString("license") ?: "غير متوفر",
                                        phone = doc.getString("phone") ?: "غير متوفر",
                                        description = doc.getString("description") ?: ""
                                    )
                                )
                            }
                        }
                        _pendingOrganizations.value = alternativeList
                    }
                }
            }
    }

    /**
     * 👑 اعتماد وتوثيق المؤسسة وتغيير حالتها في الفايرستور
     */
    fun adminApproveOrganization(orgUid: String) {
        viewModelScope.launch {
            _adminUiState.value = UiState.Loading
            try {
                val updateData = mapOf(
                    "status" to "Approved",
                    "verified" to true
                )

                db.collection("organizations")
                    .document(orgUid)
                    .update(updateData)
                    .await()

                Log.d("AdminViewModel", "Admin successfully approved organization: $orgUid")
                _adminUiState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Admin approval failed: ${e.message}")
                _adminUiState.value = UiState.Error(
                    e.localizedMessage ?: "حدث خطأ أثناء اعتماد المؤسسة من الأدمن"
                )
            }
        }
    }

    fun resetAdminUiState() {
        _adminUiState.value = UiState.Idle
    }

    /**
     * 🧹 تنظيف الذاكرة وإغلاق اتصالات الـ Realtime بمجرد خروج الأدمن أو تدمير الـ ViewModel
     */
    override fun onCleared() {
        super.onCleared()
        pendingOrgsListener?.remove()
        pendingOrgsListener = null
        Log.d("AdminViewModel", "Pending organizations listener removed successfully.")
    }
}