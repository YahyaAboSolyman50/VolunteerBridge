package com.example.volunteerbridge.network

import androidx.browser.trusted.Token
import com.example.volunteerbridge.data.model.request.*
import com.example.volunteerbridge.data.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface VolunteerBridgeApiService {

    // ==========================================
    // 📋 Activities Endpoints
    // ==========================================

    @GET("activities/")
    suspend fun getActivities(): Response<List<ActivityResponse>>

    @GET("activities/attendance/")
    suspend fun getActivitiesAttendanceList(): Response<List<VolunteerAttendanceResponse>>

    @POST("activities/attendance/")
    suspend fun createActivitiesAttendance(
        @Body attendance: VolunteerAttendanceRequest
    ): Response<VolunteerAttendanceResponse>

    @POST("activities/create/")
    suspend fun createActivity(
        @Body activity: ActivityRequest
    ): Response<ActivityResponse>

    @GET("activities/daily-logs/")
    suspend fun getDailyLogsList(): Response<List<DailyActivityLogResponse>>

    @POST("activities/daily-logs/")
    suspend fun createDailyLog(
        @Body log: DailyActivityLogRequest
    ): Response<DailyActivityLogResponse>

    @GET("activities/daily-logs/report/")
    suspend fun getDailyLogsReport(): Response<GenericReportResponse>

    @GET("activities/daily-logs/statistics/")
    suspend fun getDailyLogsStatistics(): Response<Map<String, Any>>

    @GET("activities/dashboard/")
    suspend fun getActivitiesDashboard(): Response<List<ActivityResponse>>

    @GET("activities/my-participations/")
    suspend fun getMyParticipations(): Response<List<ParticipationResponse>>

    @GET("activities/my-total-hours/")
    suspend fun getMyTotalHours(): Response<TotalHoursResponse>

    @GET("activities/organizations-report/")
    suspend fun getOrganizationsReport(): Response<GenericReportResponse>

    @GET("activities/volunteer-hours-report/")
    suspend fun getVolunteerHoursReport(): Response<List<GenericReportResponse>>

    @GET("activities/{id}/")
    suspend fun getActivityById(
        @Path("id") activityId: Int
    ): Response<ActivityResponse>

    @POST("activities/{id}/join/")
    suspend fun joinActivity(
        @Path("id") activityId: Int
    ): Response<Unit>

    @GET("activities/{id}/applications/")
    suspend fun getActivityApplications(
        @Path("id") activityId: Int
    ): Response<List<ParticipationResponse>>

    @GET("activities/my-applications/")
    suspend fun getMyApplications(): Response<List<ParticipationResponse>>

    @PUT("activities/{id}/update/")
    suspend fun updateActivity(
        @Path("id") activityId: Int,
        @Body activity: ActivityRequest
    ): Response<ActivityResponse>

    @PATCH("activities/{id}/update/")
    suspend fun partialUpdateActivity(
        @Path("id") activityId: Int,
        @Body request: ActivityRequest
    ): Response<ActivityResponse>

    @POST("activities/participations/{id}/approve/")
    suspend fun approveParticipation(
        @Path("id") participationId: Int
    ): Response<Unit>

    @POST("activities/participations/{id}/reject/")
    suspend fun rejectParticipation(
        @Path("id") participationId: Int
    ): Response<Unit>

    @POST("activities/participations/{id}/cancel/")
    suspend fun cancelParticipation(
        @Path("id") participationId: Int
    ): Response<Unit>

    @POST("activities/participations/{id}/attendance/")
    suspend fun recordParticipationAttendance(
        @Path("id") participationId: Int
    ): Response<Unit>

    @POST("activities/participations/{id}/complete/")
    suspend fun completeParticipation(
        @Path("id") participationId: Int,
        @Body body: Map<String, Int>
    ): Response<okhttp3.ResponseBody>

    // ==========================================
    // 🔑 Auth Endpoints
    // ==========================================

    @POST("auth/login/")
    suspend fun login(
        @Body credentials: CustomTokenObtainPairRequest
    ): Response<LoginResponse>

    @POST("auth/refresh/")
    suspend fun refreshAccessToken(
        @Body refreshBody: TokenRefreshRequest
    ): Response<TokenResponse>

    @GET("auth/profile/")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    // ==========================================
    // 🏢 Organizations Endpoints
    // ==========================================
    @POST("organizations/login/")
    suspend fun loginOrganization(
        @Body credentials: OrgLoginRequest
    ): Response<LoginResponse>

    @POST("organizations/")
    suspend fun registerOrganization(
        @Body organization: OrganizationRequest
    ): Response<OrganizationResponse>

    @GET("organizations/")
    suspend fun getOrganizationsList(): Response<List<OrganizationResponse>>

    @GET("activities/my-activities/")
    suspend fun getActivitiesMyOrg(): Response<List<ActivityResponse>>

    @GET("organizations/profile/")
    suspend fun getOrganizationProfile(
    ): Response<OrganizationResponse>

    @GET("organizations/my-organization/")
    suspend fun getMyOrganization(): Response<OrganizationResponse>

    @GET("organizations/pending/")
    suspend fun getPendingOrganizations(): Response<List<OrganizationResponse>>

    @GET("organizations/{id}/")
    suspend fun getOrganizationById(
        @Path("id") id: Int
    ): Response<OrganizationResponse>

    @PUT("organizations/{id}/")
    suspend fun updateOrganization(
        @Path("id") id: Int,
        @Body organization: OrganizationRequest
    ): Response<OrganizationResponse>

    @PATCH("organizations/{id}/")
    suspend fun partialUpdateOrganization(
        @Path("id") id: Int,
        @Body organization: OrganizationRequest
    ): Response<OrganizationResponse>

    @DELETE("organizations/{id}/")
    suspend fun deleteOrganization(
        @Path("id") id: Int
    ): Response<Unit>

    @POST("organizations/{id}/approve/")
    suspend fun approveOrganization(
        @Path("id") id: Int
    ): Response<Unit>

    @POST("organizations/{id}/reject/")
    suspend fun rejectOrganization(
        @Path("id") id: Int
    ): Response<Unit>
}