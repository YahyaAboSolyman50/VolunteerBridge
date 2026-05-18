package com.example.volunteerbridge.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.volunteerbridge.view.nav_bottom.org.opps.CreateOppScreen // ✨ استيراد الصفحة الجديدة هنا
import com.example.volunteerbridge.view.nav_bottom.org.home.NotificationScreenOrg
import com.example.volunteerbridge.view.nav_bottom.org.home.OpportunityStatsScreen
import com.example.volunteerbridge.view.nav_bottom.stu.home.OpportunityDetailScreen
import com.example.volunteerbridge.view.nav_bottom.org.home.OrganizationHomeContent
import com.example.volunteerbridge.view.nav_bottom.org.profile.EditOrgProfileScreen
import com.example.volunteerbridge.view.nav_bottom.stu.MyApplicationsScreen
import com.example.volunteerbridge.view.nav_bottom.stu.NotificationScreenStu
import com.example.volunteerbridge.view.nav_bottom.stu.StudentProfileScreen
import com.example.volunteerbridge.view.nav_bottom.stu.home.StudentHomeContent
import com.example.volunteerbridge.viewmodel.ApplicationsViewModel
import com.example.volunteerbridge.viewmodel.AuthViewModel
import com.example.volunteerbridge.viewmodel.NotViewModel
import com.example.volunteerbridge.viewmodel.OpportunityViewModel
import com.example.volunteerbridge.viewmodel.OrgViewModel
import com.example.volunteerbridge.viewmodel.StudentViewModel
import com.example.volunteerbridge.viewmodel.TaskViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    rootNavController: NavController,
    modifier: Modifier = Modifier,
    userType: UserType,
    authViewModel: AuthViewModel,
    oppViewModel: OpportunityViewModel,
    notViewModel: NotViewModel,
    orgViewModel: OrgViewModel,
    stuViewModel: StudentViewModel,
    taskViewModel: TaskViewModel,
    appViewModel: ApplicationsViewModel
) {
    val startDest = when (userType) {
        is UserType.Student -> Destination.Student.Home.route
        is UserType.Organization -> Destination.Organization.Dashboard.route
        else -> ""
    }
    if (startDest.isNotEmpty()) {
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = modifier
        ) {
            // --- وجهات الطالب (Student Routes) ---
            composable(Destination.Student.Home.route) {
                StudentHomeContent(orgViewModel, stuViewModel, oppViewModel, navController)
            }
            composable(Destination.Student.MyTasks.route) {
                MyApplicationsScreen(appViewModel)
            }
            composable(Destination.Student.Explore.route) {
                NotificationScreenStu(notViewModel,stuViewModel)
            }
            composable(Destination.Student.Profile.route) {
                StudentProfileScreen(stuViewModel){
                    authViewModel.logout()
                    rootNavController.navigate(Screen.LoginScreen.rout) {
                        popUpTo(0)
                    }
                }
            }

            // --- وجهات المؤسسة (Organization Routes) ---
            composable(Destination.Organization.Dashboard.route) {
                OrganizationHomeContent(
                    orgViewModel,
                    oppViewModel,
                    notViewModel,
                    { navController.navigate(SubClasses.SubClassesOrg.Notification.route) })
                { oppId ->
                    navController.navigate("${SubClasses.SubClassesOrg.OpportunityStats.route}/$oppId")
                }
            }

            // 🛠️ تحديث شاشة إدارة الفرص لاستقبال بارامتر التنقل لصفحة الإضافة الجديدة
            composable(Destination.Organization.Manage.route) {
                ManageScreen(
                    orgViewModel = orgViewModel,
                    oppViewModel = oppViewModel,
                    onEditClick = { oppId ->
                        navController.navigate("${SubClasses.SubClassesOrg.OpportunityEdit.route}/$oppId")
                    },
                    onViewDetailOppClick = { oppId ->
                        navController.navigate("${SubClasses.SubClassesOrg.OpportunityStats.route}/$oppId")
                    },
                    onCreateOppClick = {
                        navController.navigate(SubClasses.SubClassesOrg.CreateOpportunity.route)
                    }
                )
            }

            composable(Destination.Organization.Applications.route) {
                ApplicationsScreen(oppViewModel, orgViewModel)
            }
            composable(Destination.Organization.Profile.route) {
                OrganizationProfileScreen(orgViewModel, {
                    navController.navigate(SubClasses.SubClassesOrg.EditeProfile.route)
                }, {
                    authViewModel.logout()
                    rootNavController.navigate(Screen.LoginScreen.rout) {
                        popUpTo(0)
                    }
                })
            }

            composable(
                route = "${SubClasses.SubClassesStu.OppDetail.route}/{oppId}",
                arguments = listOf(navArgument("oppId") { type = NavType.StringType })
            ) { backStackEntry ->
                val oppId = backStackEntry.arguments?.getString("oppId") ?: ""
                OpportunityDetailScreen(oppId, oppViewModel,stuViewModel)
            }
            composable(
                route = SubClasses.SubClassesOrg.Notification.route,
            ) {
                NotificationScreenOrg(notViewModel, orgViewModel)
            }
            composable("${SubClasses.SubClassesOrg.OpportunityStats.route}/{oppId}") { backStackEntry ->
                val oppId = backStackEntry.arguments?.getString("oppId") ?: ""
                OpportunityStatsScreen(
                    oppId,
                    orgViewModel,
                    oppViewModel,
                    { navController.popBackStack() }
                ) { id ->
                    navController.navigate("${SubClasses.SubClassesOrg.ManageVolunteers}/$id")
                }
            }
            composable("${SubClasses.SubClassesOrg.OpportunityEdit.route}/{oppId}") { backStackEntry ->
                val oppId = backStackEntry.arguments?.getString("oppId") ?: ""
                EditOpportunityScreen(oppId, oppViewModel) { navController.popBackStack() }
            }
            composable(SubClasses.SubClassesOrg.EditeProfile.route) {
                EditOrgProfileScreen(navController, orgViewModel)
            }

            composable(SubClasses.SubClassesOrg.CreateOpportunity.route) {
                CreateOppScreen(
                    onSuccess = {
                        navController.popBackStack() // العودة بعد نجاح عملية النشر
                        // تحديث البيانات فور العودة
                        oppViewModel.fetchOrgData(orgViewModel.currentOrgData.value?.uid ?: "")
                    },
                    onBackClick = {
                        navController.popBackStack() // العودة عند ضغط سهم الرجوع الخلفي
                    },
                    oppViewModel = oppViewModel,
                    orgViewModel = orgViewModel
                )
            }

            composable(
                route = "${SubClasses.SubClassesOrg.ManageVolunteers}/{oppId}",
                arguments = listOf(navArgument("oppId") { type = NavType.StringType })
            ) { backStackEntry ->
                val oppId = backStackEntry.arguments?.getString("oppId") ?: ""

                val oppList by oppViewModel.orgOpp
                val opportunity = remember(oppList, oppId) { oppList.find { it.id == oppId } }

                if (opportunity != null) {
                    ManageVolunteersScreen(
                        opportunity = opportunity,
                        orgViewModel = orgViewModel,
                        onBackClick = { navController.popBackStack() }
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