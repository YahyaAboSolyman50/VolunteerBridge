package com.example.volunteerbridge.data.model

import com.google.gson.annotations.SerializedName

// 1. إذا كنت تريد إرسالها أو مقارنتها كـ String مباشرة:
val statusString: String = Status.PENDING.name.lowercase() // سينتج: "pending"

// 2. يمكنك تعديل الـ Enum ليحتفظ بالقيمة الـ String داخله ليسهل عليك جلبها:
enum class Status(val value: String) {
    @SerializedName("pending")
    PENDING("pending"),

    @SerializedName("approved")
    APPROVED("approved"),

    @SerializedName("rejected")
    REJECTED("rejected")
}