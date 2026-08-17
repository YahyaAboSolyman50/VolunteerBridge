package com.example.volunteerbridge.viewmodelApi

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.volunteerbridge.model.classes.NotificationModel
import com.google.firebase.firestore.FirebaseFirestore

class NotViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // قائمة الإشعارات
    private val _notifications = mutableStateOf<List<NotificationModel>>(emptyList())
    val notifications = _notifications

    private val _hasNewNotifications = mutableStateOf(false)
    val hasNewNotifications = _hasNewNotifications

    private val _isLoading = mutableStateOf(true)
    val isLoading = _isLoading

    /**
     * جلب الاستماع اللحظي (Real-time listener) للإشعارات
     */
    /**
     * جلب الاستماع اللحظي (Real-time listener) للإشعارات
     */
    fun fetchNotifications(userId: Int?) {
        if (userId == null || userId <= 0) {
            _isLoading.value = false
            _notifications.value = emptyList()
            return
        }

        // ضبط حالة التحميل إلى true عند بدء الطلب
        _isLoading.value = true

        db.collection("notifications")
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { snapshot, e ->
                // إيقاف الـ loading بغض النظر عن النتيجة (نجاح أو خطأ أو فارغ)
                _isLoading.value = false

                if (e != null) {
                    _notifications.value = emptyList()
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    val notif = doc.toObject(NotificationModel::class.java)
                    notif?.copy(notificationId = doc.id)
                }?.sortedByDescending { it.timestamp } ?: emptyList()

                _notifications.value = list
                _hasNewNotifications.value = list.any { !it.isRead }
            }
    }

    /**
     * تحديث حالة إشعار فردي إلى مقروء
     */
    fun markAsRead(notificationId: String) {
        if (notificationId.isEmpty()) {
            Log.e("NotViewModel", "Error: notificationId is empty. Cannot update Firestore.")
            return
        }

        db.collection("notifications").document(notificationId)
            .update("isRead", true)
            .addOnFailureListener { e ->
                Log.e("NotViewModel", "Failed to mark as read: ${e.message}")
            }
    }

    /**
     * تحديث كافة الإشعارات غير المقروءة إلى مقروءة دفعة واحدة
     */
    fun markAllAsRead(receiverId: Int) {
        db.collection("notifications")
            .whereEqualTo("receiverId", receiverId)
            .whereEqualTo("isRead", false)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) return@addOnSuccessListener

                val batch = db.batch()
                for (document in documents) {
                    batch.update(document.reference, "isRead", true)
                }
                batch.commit().addOnFailureListener { e ->
                    Log.e("NotViewModel", "Error marking all read: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("NotViewModel", "Error querying unread notifications: ${e.message}")
            }
    }

    /**
     * إرسال وإضافة إشعار جديد إلى Firestore
     */
    fun sendNotification(
        notModel: NotificationModel,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val notificationMap = hashMapOf(
            "receiverId" to notModel.receiverId,
            "senderId" to notModel.senderId,
            "title" to notModel.title,
            "message" to notModel.message,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false,
            "type" to notModel.type
        )

        db.collection("notifications")
            .add(notificationMap)
            .addOnSuccessListener { documentReference ->
                Log.d("NotViewModel", "Notification sent successfully with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("NotViewModel", "Failed to send notification: ${e.message}")
                onFailure(e)
            }
    }
}