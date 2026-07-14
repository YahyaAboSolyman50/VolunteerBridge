package com.example.volunteerbridge.network

import com.example.volunteerbridge.data.model.request.*
import com.example.volunteerbridge.data.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface VolunteerBridgeApiService {

    // ==========================================
    // 📋 Activities Endpoints (14 Endpoints)
    // ==========================================

    @GET("activities/")
    suspend fun getActivitiesList(): Response<List<ActivityResponse>>

    @GET("activities/attendance/")
    suspend fun getActivitiesAttendanceList(
        @Header("Authorization") token: String
    ): Response<List<VolunteerAttendanceResponse>>

    @POST("activities/attendance/")
    suspend fun createActivitiesAttendance(
        @Header("Authorization") token: String,
        @Body attendance: VolunteerAttendanceRequest
    ): Response<VolunteerAttendanceResponse>

    @POST("activities/create/")
    suspend fun createActivity(
        @Header("Authorization") token: String,
        @Body activity: ActivityRequest
    ): Response<ActivityResponse>

    @GET("activities/daily-logs/")
    suspend fun getDailyLogsList(
        @Header("Authorization") token: String
    ): Response<List<DailyActivityLogResponse>>

    @POST("activities/daily-logs/")
    suspend fun createDailyLog(
        @Header("Authorization") token: String,
        @Body log: DailyActivityLogRequest
    ): Response<DailyActivityLogResponse>

    @GET("activities/daily-logs/report/")
    suspend fun getDailyLogsReport(
        @Header("Authorization") token: String
    ): Response<GenericReportResponse>

    @GET("activities/daily-logs/statistics/")
    suspend fun getDailyLogsStatistics(
        @Header("Authorization") token: String
    ): Response<Map<String, Any>>

    @GET("activities/dashboard/")
    suspend fun getActivitiesDashboard(
        @Header("Authorization") token: String
    ): Response<DashboardData>

    @GET("activities/my-participations/")
    suspend fun getMyParticipations(
        @Header("Authorization") token: String
    ): Response<List<ParticipationResponse>>

    @GET("activities/my-total-hours/")
    suspend fun getMyTotalHours(
        @Header("Authorization") token: String
    ): Response<TotalHoursResponse>

    @GET("activities/organizations-report/")
    suspend fun getOrganizationsReport(
        @Header("Authorization") token: String
    ): Response<GenericReportResponse>

    @GET("activities/volunteer-hours-report/")
    suspend fun getVolunteerHoursReport(
        @Header("Authorization") token: String
    ): Response<GenericReportResponse>

    @POST("activities/{id}/join/")
    suspend fun joinActivity(
        @Header("Authorization") token: String,
        @Path("id") activityId: Int
    ): Response<Unit>

    // ==========================================
    // 🔑 Auth Endpoints (2 Endpoints)
    // ==========================================

    @POST("auth/login/")
    suspend fun login(
        @Body credentials: CustomTokenObtainPairRequest
    ): Response<TokenResponse>

    @POST("auth/refresh/")
    suspend fun refreshAccessToken(
        @Body refreshBody: TokenRefreshRequest
    ): Response<TokenResponse>

    // ==========================================
    // 🏢 Organizations Endpoints (الروابط الجديدة والمحدثة)
    // ==========================================

    @GET("organizations/")
    suspend fun getOrganizationsList(): Response<List<OrganizationResponse>>

    @POST("organizations/")
    suspend fun createOrganization(
        @Header("Authorization") token: String,
        @Body organization: OrganizationRequest
    ): Response<OrganizationResponse>

    // 🆕 تسجيل دخول مؤسسة
    @POST("organizations/login/")
    suspend fun organizationLogin(
        @Body credentials: CustomTokenObtainPairRequest // أو الموديل الخاص بتسجيل دخولهم
    ): Response<TokenResponse>

    // 🆕 جلب المؤسسات قيد الانتظار للموافقة عليها (Admin)
    @GET("organizations/pending/")
    suspend fun getPendingOrganizations(
        @Header("Authorization") token: String
    ): Response<List<OrganizationResponse>>

    // 🆕 إنشاء حساب مؤسسة جديد (طلب تسجيل)
    @POST("organizations/register/")
    suspend fun registerOrganization(
        @Body organization: OrganizationRequest
    ): Response<OrganizationResponse>

    @GET("organizations/{id}/")
    suspend fun getOrganizationById(
        @Path("id") id: Int
    ): Response<OrganizationResponse>

    @PUT("organizations/{id}/")
    suspend fun updateOrganization(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body organization: OrganizationRequest
    ): Response<OrganizationResponse>

    @PATCH("organizations/{id}/")
    suspend fun partialUpdateOrganization(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body partialData: Map<String, Any?>
    ): Response<OrganizationResponse>

    @DELETE("organizations/{id}/")
    suspend fun deleteOrganization(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    // 🆕 قبول طلب تسجيل المؤسسة من قبل الأدمن
    @POST("organizations/{id}/approve/")
    suspend fun approveOrganization(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    // 🆕 رفض طلب تسجيل المؤسسة من قبل الأدمن
    @POST("organizations/{id}/reject/")
    suspend fun rejectOrganization(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

    // ==========================================
    // 👤 Profile Endpoint
    // ==========================================

    @GET("auth/profile/")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>
}