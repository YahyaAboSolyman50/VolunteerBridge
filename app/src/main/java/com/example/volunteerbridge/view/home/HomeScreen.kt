package com.example.volunteerbridge.view.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.volunteerbridge.model.UserType
import com.example.volunteerbridge.viewmodel.AuthViewModel
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.volunteerbridge.model.classes.Destination
import com.example.volunteerbridge.nav.AppNavHost
import com.example.volunteerbridge.screens.Screen
import com.example.volunteerbridge.view.admin.AdminDashboardScreen
import com.example.volunteerbridge.viewmodel.AdminViewModel
import com.example.volunteerbridge.viewmodel.ApplicationsViewModel
import com.example.volunteerbridge.viewmodel.NotViewModel
import com.example.volunteerbridge.viewmodel.OpportunityViewModel
import com.example.volunteerbridge.viewmodel.OrgViewModel
import com.example.volunteerbridge.viewmodel.StudentViewModel
import com.example.volunteerbridge.viewmodel.TaskViewModel

@Composable
fun HomeScreen(
    rootNavController: NavHostController,
    authViewModel: AuthViewModel,
    oppViewModel: OpportunityViewModel,
    notViewModel: NotViewModel,
    orgViewModel: OrgViewModel,
    stuViewModel: StudentViewModel,
    taskViewModel: TaskViewModel,
    adminViewModel: AdminViewModel,
    appViewModel: ApplicationsViewModel
) {
    val userType by authViewModel.userType.collectAsState()
    val isEmailVerified by remember { orgViewModel.isEmailVerified }

    // مراقبة حالة توثيق المؤسسة الحالية من الـ Firestore
    val currentOrgData by orgViewModel.currentOrgData
    val isVerified = currentOrgData?.verified ?: false

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        authViewModel.checkUserAndData()
    }

    LaunchedEffect(userType) {
        when (userType) {
            is UserType.Student -> {
                val studentId = authViewModel.auth.currentUser?.uid
                studentId?.let { id ->
                    oppViewModel.fetchAllForStudent()
                    stuViewModel.fetchUserApplications(id)
                    notViewModel.fetchNotifications(id)
                    stuViewModel.fetchCurrentStudentProfile()
                    appViewModel.fetchStudentApplications()
                }
            }

            is UserType.Organization -> {
                val uid = authViewModel.auth.currentUser?.uid
                uid?.let { id ->
                    orgViewModel.checkEmailVerificationStatus()
                    orgViewModel.fetchCurrentOrgProfile()
                    oppViewModel.fetchOrgData(id)
                    orgViewModel.fetchApplicationsForOrg(id)
                    notViewModel.fetchNotifications(id)
                }
            }

            is UserType.Admin -> {
                // 👑 فور دخول الأدمن، يتم تفعيل الاستماع الحي للطلبات المعلقة
                adminViewModel.fetchPendingOrganizationsForAdmin()
            }
            else -> {}
        }
    }

    Scaffold(
        bottomBar = {
            val rootScreens = listOf(
                Destination.Student.Home.route,
                Destination.Student.MyTasks.route,
                Destination.Student.Explore.route,
                Destination.Student.Profile.route,
                Destination.Organization.Dashboard.route,
                Destination.Organization.Manage.route,
                Destination.Organization.Applications.route,
                Destination.Organization.Profile.route
            )

            // نمنع إظهار الـ BottomBar إذا كان الحساب أدمن أو إذا كانت المؤسسة غير موثقة بعد
            val shouldShowBottomBar = currentRoute in rootScreens &&
                    (userType !is UserType.Organization || isEmailVerified) &&
                    (userType !is UserType.Admin)

            if (shouldShowBottomBar) {
                when (userType) {
                    is UserType.Student -> {
                        val studentScreens = listOf(
                            Destination.Student.Home,
                            Destination.Student.MyTasks,
                            Destination.Student.Explore,
                            Destination.Student.Profile
                        )
                        AppBottomNavigation(currentRoute, navController, studentScreens)
                    }

                    is UserType.Organization -> {
                        val orgScreens = listOf(
                            Destination.Organization.Dashboard,
                            Destination.Organization.Manage,
                            Destination.Organization.Applications,
                            Destination.Organization.Profile
                        )
                        AppBottomNavigation(currentRoute, navController, orgScreens)
                    }
                    else -> {}
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 🛡️ فحص دفاعي استباقي: نتحقق برمجياً إذا كان الحساب المسجل حالياً هو حساب الأدمن لعزله تماماً عن حالات الخطأ الفرعية
            when (userType) {
                is UserType.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF042A63))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Preparing your workspace...", color = Color.Gray)
                        }
                    }
                }

                // 👑 توجيه الأدمن لشاشته الخاصة فوراً وعزله عن مسارات التعليق
                is UserType.Admin -> {
                    AdminDashboardScreen(
                        authViewModel = authViewModel,
                        adminViewModel = adminViewModel,
                        onLogoutSuccess = {
                            rootNavController.navigate(Screen.LoginScreen.rout) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                is UserType.Organization -> {
                    if (!isEmailVerified) {
                        UnverifiedOrgScreen(
                            orgViewModel = orgViewModel,
                            onLogoutClick = {
                                authViewModel.logout()
                                rootNavController.navigate(Screen.LoginScreen.rout) { popUpTo(0) { inclusive = true } }
                            }
                        )
                    } else {
                        AppNavHost(
                            navController = navController,
                            rootNavController = rootNavController,
                            userType = userType,
                            authViewModel = authViewModel,
                            oppViewModel = oppViewModel,
                            notViewModel = notViewModel,
                            orgViewModel = orgViewModel,
                            stuViewModel = stuViewModel,
                            taskViewModel = taskViewModel,
                            appViewModel = appViewModel
                        )
                    }
                }

                is UserType.Student -> {
                    AppNavHost(
                        navController,
                        rootNavController,
                        userType = userType,
                        authViewModel = authViewModel,
                        oppViewModel = oppViewModel,
                        notViewModel = notViewModel,
                        orgViewModel = orgViewModel,
                        stuViewModel = stuViewModel,
                        taskViewModel = taskViewModel,
                        appViewModel = appViewModel
                    )
                }

                is UserType.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Failed to load data. Please check your connection.")
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { authViewModel.checkUserAndData() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun AppBottomNavigation(
    currentRoute: String?,
    navController: NavHostController,
    destinations: List<Destination>
) {
    val colorScheme = MaterialTheme.colorScheme

    NavigationBar(
        containerColor = colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 0.dp
    ) {
        destinations.forEach { screen ->
            val isSelected = currentRoute == screen.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label,
                        tint = if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                label = {
                    Text(
                        text = screen.label,
                        color = if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.5f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}