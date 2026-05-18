package com.example.volunteerbridge.model.classes

data class OpportunityModel(
    // Identification
    val id: String = "",                 // Unique ID of the opportunity (Firestore Document ID)
    val orgId: String = "",              // Unique ID of the organization that posted the opportunity
    val orgName: String = "",            // Organization name (denormalized for fast UI rendering)
    val isOrgVerified: Boolean = false,  // Verification status of the organization

    // Classification
    val orgType: String = "",            // Organization Type: "Local NGO" (أهلية), "Government" (رسمية), "International" (دولية)

    // Content
    val title: String = "",              // Title of the opportunity (e.g., "Mobile Developer")
    val description: String = "",        // Detailed description of the tasks and roles
    val category: String = "",           // Category: (Technical, Medical, Educational, etc.)
    val location: String = "",           // Physical location or "Remote"

    // Timing & Deadlines
    val startDate: Long = 0,             // Start date of the volunteering work (Timestamp)
    val endDate: Long = 0,               // End date of the volunteering work (Timestamp)
    val deadline: Long = 0,              // Last day to apply (Timestamp)

    // Stats & Capacity
    val requiredHours: Int = 0,          // Total hours the student will earn upon completion
    val vacancies: Int = 0,              // Total number of available seats
    val applicantsCount: Int = 0,        // Current number of students who applied

    val tasks: List<String> = emptyList(),

    // Additional Details
    val requirements: List<String> = emptyList(), // List of skills or conditions (e.g., "Fluent in English")
    val tags: List<String> = emptyList(),         // Searchable tags (e.g., "UI/UX", "Gaza", "Urgent")
    val status: String = "Active",                // Current status: "Active", "Closed", "Completed"
    val createdAt: Long = System.currentTimeMillis() // Creation timestamp for sorting by latest
)