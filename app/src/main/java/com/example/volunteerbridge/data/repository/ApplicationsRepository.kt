package com.example.volunteerbridge.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ApplicationsRepository {
//    private val firestore = FirebaseFirestore.getInstance()
//    private val auth = FirebaseAuth.getInstance()
//
//    /**
//     * الحصول على الـ UID الخاص بالطالب الحالي بشكل آمن
//     */
//    fun getCurrentStudentId(): String? {
//        return auth.currentUser?.uid
//    }
//
//    /**
//     * 👀 الاستماع الحي (Realtime) لطلبات التقديم الخاصة بالطالب الحالي وتحويلها إلى Flow
//     */
//    fun listenToStudentApplications(studentId: String): Flow<List<ApplicationModel>> =
//        callbackFlow {
//            val listener = firestore.collection("applications")
//                .whereEqualTo("studentId", studentId)
//                .addSnapshotListener { snapshot, error ->
//                    if (error != null) {
//                        Log.e("ApplicationsRepository", "Listen failed: ${error.message}")
//                        close(error)
//                        return@addSnapshotListener
//                    }
//
//                    if (snapshot != null) {
//                        val list = snapshot.toObjects(ApplicationModel::class.java)
//                        // الترتيب التنازلي حسب حقل appliedAt داخل الريبوزتوري
//                        val sortedList = list.sortedByDescending { it.appliedAt }
//                        trySend(sortedList)
//                    }
//                }
//
//            // 🧹 التنظيف التلقائي لمنع الـ Memory Leak بمجرد إلغاء الاستماع
//            awaitClose {
//                listener.remove()
//                Log.d("ApplicationsRepository", "Student applications snapshot listener removed.")
//            }
//        }
//
//    /**
//     * ❌ حذف / إلغاء طلب التقديم من الفايرستور
//     */
//    suspend fun cancelApplication(appId: String) {
//        firestore.collection("applications")
//            .document(appId)
//            .delete()
//            .await()
//    }
}