package com.example.volunteerbridge.viewmodel



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



// جرب هذا مؤقتاً للتأكد من وصول البيانات

    fun fetchNotifications(userId: String) {

        db.collection("notifications")

            .whereEqualTo("receiverId", userId)

            .addSnapshotListener { snapshot, e ->

                if (e != null) {

                    Log.e("NotifError", "Error: ${e.message}")

                    return@addSnapshotListener

                }



                val list = snapshot?.documents?.mapNotNull { doc ->

                    val notif = doc.toObject(NotificationModel::class.java)

                    notif?.copy(notificationId = doc.id)

                }?.sortedByDescending { it.timestamp } ?: emptyList()



                _notifications.value = list

                _hasNewNotifications.value = list.any { !it.isRead }



                Log.d("NotifCheck", "Fetched ${list.size} notifications for $userId")

            }

    }



    fun markAsRead(notificationId: String) {

        if (notificationId.isEmpty()) {

            Log.e("NotViewModel", "Error: notificationId is empty. Cannot update Firestore.")

            return

        }



// 2. تحديث الوثيقة في Firestore

        db.collection("notifications").document(notificationId)

            .update("isRead", true)

            .addOnFailureListener { e ->

                Log.e("NotViewModel", "Failed to mark as read: ${e.message}")

            }

    }

    fun markAllAsRead(orgId: String) {

        db.collection("notifications")

            .whereEqualTo("receiverId", orgId)

            .whereEqualTo("isRead", false)

            .get()

            .addOnSuccessListener { documents ->

                val batch = db.batch()

                for (document in documents) {

                    batch.update(document.reference, "isRead", true)

                }

                batch.commit().addOnFailureListener { e ->

                    Log.e("NotViewModel", "Error marking all read: ${e.message}")

                }

            }

    }

}