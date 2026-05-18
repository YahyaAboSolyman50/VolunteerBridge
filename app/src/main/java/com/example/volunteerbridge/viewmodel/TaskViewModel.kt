package com.example.volunteerbridge.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.model.classes.VolunteerTask
import com.example.volunteerbridge.model.classes.status.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TaskViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    private val _taskUiState = MutableStateFlow<UiState<List<VolunteerTask>>>(UiState.Idle)
    val taskUiState: StateFlow<UiState<List<VolunteerTask>>> = _taskUiState.asStateFlow()

    private val _studentTasks = mutableStateOf<List<VolunteerTask>>(emptyList())
    val studentTasks = _studentTasks

    /**
     * جلب كافة المهام المسندة للطالب الحالي حياً من الـ Firestore
     */
    fun fetchTasksForStudent() {
        val studentId = auth.currentUser?.uid ?: return
        _taskUiState.value = UiState.Loading

        db.collection("tasks")
            .whereEqualTo("studentId", studentId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _taskUiState.value = UiState.Error(e.localizedMessage ?: "Failed to load tasks")
                    return@addSnapshotListener
                }

                val list = snapshot?.toObjects(VolunteerTask::class.java) ?: emptyList()
                // ترتيب المهام بحيث تظهر المهام غير المكتملة أو الأحدث أولاً
                val sortedList = list.sortedWith(compareBy<VolunteerTask> { it.status == "Completed" }.thenByDescending { it.dueDate })

                _studentTasks.value = sortedList
                _taskUiState.value = UiState.Success(sortedList)
            }
    }

    /**
     * تحديث حالة المهمة (Pending -> In Progress -> Completed)
     */
    fun updateTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                db.collection("tasks").document(taskId)
                    .update("status", newStatus)
                    .await()
                Log.d("TaskViewModel", "Task $taskId updated to $newStatus")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error updating task status: ${e.message}")
            }
        }
    }
}