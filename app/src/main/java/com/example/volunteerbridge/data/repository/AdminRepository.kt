package com.example.volunteerbridge.data.repository

import android.util.Log

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AdminRepository {
//    private val db = FirebaseFirestore.getInstance()
//
//    /**
//     * 👀 جلب كافة المؤسسات المنتظرة للتوثيق تلقائياً وبشكل حي (Realtime)
//     * تم التعديل: الفلترة تتم من خلال السيرفر .whereEqualTo لتتوافق مع الـ Rules
//     */
//    fun listenToPendingOrganizations(): Flow<List<Organization>> = callbackFlow {
//        // 🔥 الحل السحري: الفلترة مباشرة من السيرفر بناءً على حقل status
//        val query = db.collection("organizations")
//            .whereEqualTo("status", "pending")
//
//        val listener = query.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                Log.e("AdminRepository", "Admin fetch failed: ${e.message}")
//                close(e) // إغلاق الـ Flow وإرسال الخطأ للـ ViewModel ليعرض التوست
//                return@addSnapshotListener
//            }
//
//            if (snapshot != null) {
//                try {
//                    // 1. المحاولة القياسية المحمية (السيرفر فلترها وجاهزة)
//                    val pendingList = snapshot.toObjects(Organization::class.java)
//                    trySend(pendingList)
//                    Log.d("AdminRepository", "Fetched successfully from server. Count: ${pendingList.size}")
//                } catch (mappingError: Exception) {
//                    Log.e("AdminRepository", "Mapping failed, using defensive fallback: ${mappingError.message}")
//
//                    // 2. الخطة الدفاعية البديلة في حال اختلاف حقول موديل كوتلن عن Firestore
//                    val alternativeList = mutableListOf<Organization>()
//                    for (doc in snapshot.documents) {
//                        alternativeList.add(
//                            Organization(
//                                uid = doc.id,
//                                nameOrg = doc.getString("nameOrg") ?: doc.getString("name") ?: "مؤسسة بدون اسم",
//                                emailOrg = doc.getString("emailOrg") ?: "",
//                                status = doc.getString("status") ?: "Pending",
//                                verified = doc.getBoolean("verified") ?: false,
//                                orgType = doc.getString("orgType") ?: "مؤسسة",
//                                license = doc.getString("license") ?: "غير متوفر",
//                                phone = doc.getString("phone") ?: "غير متوفر",
//                                description = doc.getString("description") ?: ""
//                            )
//                        )
//                    }
//                    trySend(alternativeList)
//                }
//            }
//        }
//
//        // 🧹 التنظيف التلقائي عند إغلاق الشاشة
//        awaitClose {
//            listener.remove()
//            Log.d("AdminRepository", "Pending organizations snapshot listener closed.")
//        }
//    }
//
//    /**
//     * 👑 اعتماد وتوثيق المؤسسة في الفايرستور
//     */
//    suspend fun approveOrganization(orgUid: String) {
//        val updates = hashMapOf(
//            "verified" to true,
//            "status" to "approved",
//        )
//
//        db.collection("organizations")
//            .document(orgUid)
//            .update(updates as Map<String, Any>)
//            .await()
//    }
}