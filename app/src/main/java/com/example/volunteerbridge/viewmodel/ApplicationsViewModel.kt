package com.example.volunteerbridge.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.model.classes.ApplicationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ApplicationsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _applications = MutableStateFlow<List<ApplicationModel>>(emptyList())
    val applications: StateFlow<List<ApplicationModel>> = _applications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()



    fun fetchStudentApplications() {
        val studentId = auth.currentUser?.uid ?: return
        _isLoading.value = true

        firestore.collection("applications")
            .whereEqualTo("studentId", studentId)
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false
                if (error != null) {
                    Log.e("ApplicationsViewModel", "Listen failed: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.toObjects(ApplicationModel::class.java)
                    // 🟢 التعديل: الترتيب التنازلي حسب حقل البيانات الخاص بك appliedAt
                    _applications.value = list.sortedByDescending { it.appliedAt }
                }
            }
    }

    fun cancelApplication(appId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                firestore.collection("applications")
                    .document(appId)
                    .delete()
                    .await()
                onResult(true)
            } catch (e: Exception) {
                Log.e("ApplicationsViewModel", "Error canceling application: ${e.message}")
                onResult(false)
            }
        }
    }
}