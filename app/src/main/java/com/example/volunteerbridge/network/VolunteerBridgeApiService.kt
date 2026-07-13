package com.example.volunteerbridge.network

import com.example.volunteerbridge.data.model.entities.UserModel
import com.example.volunteerbridge.data.model.entities.LoginRequest
import com.example.volunteerbridge.data.model.entities.LoginResponse
import com.example.volunteerbridge.data.model.entities.dto.ActivityRequest
import com.example.volunteerbridge.data.model.entities.dto.ActivityResponse
import com.example.volunteerbridge.data.model.entities.dto.ParticipationResponse
import com.example.volunteerbridge.data.model.entities.dto.TotalHoursResponse
import retrofit2.Response
import retrofit2.http.*

interface VolunteerBridgeApiService {

    // ==========================================
    // 🔐 AUTH ENDPOINTS
    // ==========================================

    @POST("auth/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

//    @POST("auth/refresh/")
//    suspend fun refreshToken(
//        @Body request: RefreshTokenRequest
//    ): Response<TokenResponse>

    @GET("auth/user/") // أو مسار البروفايل لديك
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<UserModel>

    @GET("activities/my-participations/")
    suspend fun getMyParticipations(
        @Header("Authorization") token: String
    ): Response<List<ParticipationResponse>>


    // ==========================================
    // 🎯 ACTIVITIES ENDPOINTS
    // ==========================================

    @GET("activities/")
    suspend fun getAllActivities(): Response<List<ActivityResponse>>

    @POST("activities/create/")
    suspend fun createActivity(
        @Body request: ActivityRequest
    ): Response<ActivityResponse>

    @POST("activities/{id}/join/")
    suspend fun joinActivity(
        @Header("Authorization") token: String,
        @Path("id") activityId: Int
    ): Response<Unit>
    @GET("activities/my-participations/")
    suspend fun getMyParticipations(): Response<List<ParticipationResponse>>

    @GET("activities/my-total-hours/")
    suspend fun getMyTotalHours(): Response<TotalHoursResponse>

//    @GET("activities/dashboard/")
//    suspend fun getActivitiesDashboard(): Response<DashboardResponse>


    // ==========================================
    // 📝 ATTENDANCE & LOGS ENDPOINTS
    // ==========================================

//    @GET("activities/attendance/")
//    suspend fun getAttendanceList(): Response<List<AttendanceResponse>>

//    @POST("activities/attendance/")
//    suspend fun createAttendance(
//        @Body request: AttendanceRequest
//    ): Response<AttendanceResponse>

//    @GET("activities/daily-logs/")
//    suspend fun getDailyLogs(): Response<List<DailyLogResponse>>
//
//    @POST("activities/daily-logs/")
//    suspend fun createDailyLog(
//        @Body request: DailyLogRequest
//    ): Response<DailyLogResponse>


    // ==========================================
    // 📊 REPORTS & STATISTICS ENDPOINTS
    // ==========================================

//    @GET("activities/daily-logs/report/")
//    suspend fun getDailyLogsReport(): Response<List<DailyLogReportResponse>>
//
//    @GET("activities/daily-logs/statistics/")
//    suspend fun getDailyLogsStatistics(): Response<StatisticsResponse>
//
//    @GET("activities/organizations-report/")
//    suspend fun getOrganizationsReport(): Response<List<OrgReportResponse>>
//
//    @GET("activities/volunteer-hours-report/")
//    suspend fun getVolunteerHoursReport(): Response<List<VolunteerReportResponse>>


    // ==========================================
    // 🏢 ORGANIZATIONS ENDPOINTS
    // ==========================================

//    @GET("organizations/")
//    suspend fun getOrganizationsList(): Response<List<OrganizationResponse>>
//
//    @POST("organizations/")
//    suspend fun createOrganization(
//        @Body request: OrganizationRequest
//    ): Response<OrganizationResponse>
//
//    @GET("organizations/{id}/")
//    suspend fun getOrganizationById(
//        @Path("id") orgId: Int
//    ): Response<OrganizationResponse>
//
//    @PUT("organizations/{id}/")
//    suspend fun updateOrganization(
//        @Path("id") orgId: Int,
//        @Body request: OrganizationRequest
//    ): Response<OrganizationResponse>
//
//    @PATCH("organizations/{id}/")
//    suspend fun partialUpdateOrganization(
//        @Path("id") orgId: Int,
//        @Body request: Map<String, Any>
//    ): Response<OrganizationResponse>

//    @DELETE("organizations/{id}/")
//    suspend fun deleteOrganization(
//        @Path("id") orgId: Int
//    ): Response<Unit>
}