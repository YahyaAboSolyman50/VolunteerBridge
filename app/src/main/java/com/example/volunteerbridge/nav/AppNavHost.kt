package com.example.volunteerbridge.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.volunteerbridge.model.UserType
import com.example.volunteerbridge.model.classes.Destination
import com.example.volunteerbridge.model.classes.SubClasses
import com.example.volunteerbridge.screens.Screen
import com.example.volunteerbridge.view.nav_bottom.org.ApplicationsScreen
import com.example.volunteerbridge.view.nav_bottom.org.profile.OrganizationProfileScreen
import com.example.volunteerbridge.view.nav_bottom.org.home.EditOpportunityScreen
import com.example.volunteerbridge.view.nav_bottom.org.home.ManageVolunteersScreen
import com.example.volunteerbridge.view.nav_bottom.org.opps.ManageScreen
import com.example.volunteerbridge.view.nav_bottom.org.opps.CreateOppScreen
import com.example.volunteerbridge.view.nav_bottom.org.home.NotificationScreenOrg
import com.example.volunteerbridge.view.nav_bottom.org.home.OpportunityStatsScreen
import com.example.volunteerbridge.view.nav_bottom.stu.home.OpportunityDetailScreen
import com.example.volunteerbridge.view.nav_bottom.org.home.OrganizationHomeContent
import com.example.volunteerbridge.view.nav_bottom.org.profile.EditOrgProfileScreen
import com.example.volunteerbridge.view.nav_bottom.stu.AllOpportunitiesScreen
import com.example.volunteerbridge.view.nav_bottom.stu.MyApplicationsScreen
import com.example.volunteerbridge.view.nav_bottom.stu.home.NotificationScreenStu
import com.example.volunteerbridge.view.nav_bottom.stu.StudentProfileScreen
import com.example.volunteerbridge.view.nav_bottom.stu.home.StudentHomeContent
import com.example.volunteerbridge.viewmodelApi.NotViewModel
import com.example.volunteerbridge.viewmodelApi.ActivityViewModel
import com.example.volunteerbridge.viewmodelApi.AttendanceViewModel
import com.example.volunteerbridge.viewmodelApi.AuthViewModelApi
import com.example.volunteerbridge.viewmodelApi.OrganizationViewModel
import com.example.volunteerbridge.viewmodelApi.ParticipationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navController: NavHostController,
    rootNavController: NavController,
    modifier: Modifier = Modifier,
    userType: UserType,
    notViewModel: NotViewModel,
    activityViewModel: ActivityViewModel,
    authViewModelApi: AuthViewModelApi,
    organizationViewModel: OrganizationViewModel,
    studentViewModel: com.example.volunteerbridge.viewmodelApi.StudentViewModel,
    participationViewModel: ParticipationViewModel,
    attendanceViewModel: AttendanceViewModel,
    token: String?
) {
    // تحديد الشاشة الافتتاحية بناءً على نوع المستخدم (طالب أو مؤسسة)
    val startDest = when (userType) {
        is UserType.Student, is UserType.Volunteer -> Destination.Student.Home.route
        is UserType.Organization -> Destination.Organization.Dashboard.route
        else -> ""
    }

    if (startDest.isNotEmpty()) {
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = modifier
        ) {
            // ==========================================
            // --- 🎓 وجهات وعروض الطالب (Student Routes) ---
            // ==========================================

            // الشاشة الرئيسية للطالب
            composable(Destination.Student.Home.route) {
                StudentHomeContent(
                    activityViewModel = activityViewModel,
                    authViewModelApi = authViewModelApi,
                    studentViewModel = studentViewModel,
                    notViewModel = notViewModel,
                    navController = navController,
                    token = token ?: ""
                )
            }

            // شاشة مهام وطلبات انضمام الطالب
            composable(Destination.Student.MyTasks.route) {
                MyApplicationsScreen(
                    viewModel = participationViewModel,
                    attendanceViewModel = attendanceViewModel,
                    token = token ?: "",
                    activityViewModel = activityViewModel
                )
            }

            // شاشة الإشعارات الخاصة بالطالب (تم التعديل لتمرير المكونات بشكل صحيح)
            composable(Destination.Student.AllOpp.route) {
                AllOpportunitiesScreen(
                    activityViewModel,
                    authViewModelApi,
                    navController
                )
            }

            // الملف الشخصي للطالب مع إمكانية تسجيل الخروج
            composable(Destination.Student.Profile.route) {
                StudentProfileScreen(authViewModelApi, studentViewModel) {
                    authViewModelApi.logout()
                    rootNavController.navigate(Screen.LoginScreen.rout) {
                        popUpTo(0)
                    }
                }
            }

            // ==========================================
            // --- 🏢 وجهات وعروض المؤسسة (Organization Routes) ---
            // ==========================================

            // لوحة تحكم المؤسسة الرئيسية
            composable(Destination.Organization.Dashboard.route) {
                OrganizationHomeContent(
                    organizationViewModel = organizationViewModel,
                    activityViewModel = activityViewModel,
                    authViewModelApi = authViewModelApi,
                    notViewModel = notViewModel,
                    onNotificationClick = { navController.navigate(SubClasses.SubClassesOrg.Notification.route) },
                    onTopPreClick = { oppId ->
                        navController.navigate("${SubClasses.SubClassesOrg.OpportunityStats.route}/$oppId")
                    }
                )
            }

            // شاشة إدارة فرص التطوع الخاصة بالمؤسسة
            composable(Destination.Organization.Manage.route) {
                ManageScreen(
                    onBackClick = { navController.popBackStack() },
                    onCreateOppClick = { navController.navigate(SubClasses.SubClassesOrg.CreateOpportunity.route) },
                    onEditClick = { oppId ->
                        navController.navigate("${SubClasses.SubClassesOrg.OpportunityEdit.route}/$oppId")
                    },
                    onViewDetailOppClick = { oppId ->
                        navController.navigate("${SubClasses.SubClassesOrg.OpportunityStats.route}/$oppId")
                    },
                    activityViewModel = activityViewModel,
                    userToken = token ?: ""
                )
            }

            // شاشة متابعة طلبات المتقدمين للمؤسسة
            composable(Destination.Organization.Applications.route) {
                ApplicationsScreen(participationViewModel, organizationViewModel, notViewModel)
            }

            // ملف المؤسسة الشخصي مع التنقل للتعديل أو الخروج
            composable(Destination.Organization.Profile.route) {
                OrganizationProfileScreen(
                    organizationViewModel = organizationViewModel,
                    onEditProfileClick = { navController.navigate(SubClasses.SubClassesOrg.EditeProfile.route) },
                    onLogoutClick = {
                        authViewModelApi.logout()
                        rootNavController.navigate(Screen.LoginScreen.rout) {
                            popUpTo(0)
                        }
                    }
                )
            }

            // ==========================================
            // --- 🔗 المسارات الفرعية والتفصيلية (Sub-Routes) ---
            // ==========================================

            // تفاصيل الفرصة التطوعية (للطالب)
            composable(
                route = "${SubClasses.SubClassesStu.OppDetail.route}/{oppId}",
                arguments = listOf(navArgument("oppId") { type = NavType.StringType })
            ) { backStackEntry ->
                val oppIdString = backStackEntry.arguments?.getString("oppId") ?: "0"
                val oppId = oppIdString.toIntOrNull() ?: 0

                OpportunityDetailScreen(
                    opportunityId = oppId,
                    oppViewModel = activityViewModel,
                    stuViewModel = studentViewModel,
                    authViewModelApi = authViewModelApi,
                    notificationViewModel = notViewModel
                ) {
                    navController.popBackStack()
                }
            }
            composable(
                route = SubClasses.SubClassesStu.Notification.route
            ) {
                NotificationScreenStu(
                    notViewModel,
                    studentViewModel,
                    navController
                )
            }

            // شاشة إشعارات المؤسسة
            composable(
                route = SubClasses.SubClassesOrg.Notification.route
            ) {
                NotificationScreenOrg(notViewModel, organizationViewModel,navController)
            }

            // شاشة إحصائيات الفرصة التطوعية للمؤسسة
            composable(
                route = "${SubClasses.SubClassesOrg.OpportunityStats.route}/{oppId}",
                arguments = listOf(navArgument("oppId") { type = NavType.StringType })
            ) { backStackEntry ->
                val oppId = backStackEntry.arguments?.getString("oppId") ?: ""
                OpportunityStatsScreen(
                    oppId = oppId,
                    orgViewModel = organizationViewModel,
                    oppViewModel = activityViewModel,
                    onAllAppClick = { id ->
                        navController.navigate("${SubClasses.SubClassesOrg.ManageVolunteers}/$id")
                    },
                    onManageClick = {
                        navController.navigate("${SubClasses.SubClassesOrg.OpportunityEdit.route}/$oppId")
                    },
                    onBackClick = {}
                )
            }

            // شاشة تعديل فرصة تطوعية موجودة
            composable(
                route = "${SubClasses.SubClassesOrg.OpportunityEdit.route}/{oppId}",
                arguments = listOf(navArgument("oppId") { type = NavType.StringType })
            ) { backStackEntry ->
                val oppIdString = backStackEntry.arguments?.getString("oppId") ?: "0"
                val oppId = oppIdString.toIntOrNull() ?: 0

                EditOpportunityScreen(
                    activityId = oppId,
                    activityViewModel = activityViewModel,
                    token = token ?: " ",
                    onBackClick = {}
                )
            }

            // شاشة تعديل ملف المؤسسة
            composable(SubClasses.SubClassesOrg.EditeProfile.route) {
                EditOrgProfileScreen(navController, organizationViewModel, token ?: "")
            }

            // شاشة إنشاء فرصة تطوعية جديدة
            composable(SubClasses.SubClassesOrg.CreateOpportunity.route) {
                token?.let { userToken ->
                    CreateOppScreen(
                        activityViewModel = activityViewModel,
                        userToken = userToken,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            // شاشة إدارة المتطوعين المقبولين في فرصة معينة
            composable(
                route = "${SubClasses.SubClassesOrg.ManageVolunteers}/{oppId}",
                arguments = listOf(navArgument("oppId") { type = NavType.StringType })
            ) { backStackEntry ->
                val oppId = backStackEntry.arguments?.getString("oppId") ?: ""

                val activities by activityViewModel.myActivities.collectAsState()
                val activityResponse = remember(activities, oppId) {
                    activities.find { it.id.toString() == oppId }
                }

                if (activityResponse != null) {
                    ManageVolunteersScreen(
                        token = token ?: "",
                        activityResponse = activityResponse,
                        orgViewModel = organizationViewModel,
                        participationViewModel = participationViewModel,
                        attendanceViewModel = attendanceViewModel,
                        onBackClick = {}
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}