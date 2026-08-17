package com.example.volunteerbridge.data.model

enum class OpportunityCategory(val label: String, val backendValue: String) {
    TECHNICAL("تقني وفني", "technical"),
    MEDICAL("طبي وصحي", "medical"),
    EDUCATIONAL("تعليمي وتربوي", "educational"),
    ADMINISTRATIVE("إداري ومكتبي", "administrative");

    companion object {
        // دالة لإرجاع كافة التسميات (Labels) لعرضها في القائمة المنسدلة Dropdown
        val labels: List<String> = entries.map { it.label }

        // دالة للبحث عن قيمة الـ Backend بناءً على التسمية المختارة
        fun fromLabel(label: String): String? {
            return entries.find { it.label == label }?.backendValue
        }

        // دالة اختيارية للبحث عن التسمية بناءً على قيمة السيرفر (تفيد عند التعديل Edit)
        fun fromBackendValue(value: String?): String {
            return entries.find { it.backendValue == value }?.label ?: "اختر الفئة"
        }
    }
}