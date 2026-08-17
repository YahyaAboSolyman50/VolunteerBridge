package com.example.volunteerbridge.view.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.volunteerbridge.data.model.TokenManager
import com.example.volunteerbridge.model.UserType
import com.example.volunteerbridge.model.classes.Destination
import com.example.volunteerbridge.nav.AppNavHost
import com.example.volunteerbridge.screens.Screen
import com.example.volunteerbridge.view.admin.AdminDashboardScreen
import com.example.volunteerbridge.model.classes.status.AuthState
import com.example.volunteerbridge.network.NetworkConnectivityObserver
import com.example.volunteerbridge.viewmodelApi.AdminViewModelApi
import com.example.volunteerbridge.viewmodelApi.AttendanceViewModel
import com.example.volunteerbridge.viewmodelApi.AuthViewModelApi
import com.example.volunteerbridge.viewmodelApi.NotViewModel
import com.example.volunteerbridge.viewmodelApi.OrganizationViewModel
import com.example.volunteerbridge.viewmodelApi.ParticipationViewModel
import com.example.volunteerbridge.viewmodelApi.ActivityViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    rootNavController: NavHostController,
    authViewModelApi: AuthViewModelApi,
    notViewModel: NotViewModel,
    activityViewModel: ActivityViewModel,
    adminViewModel: AdminViewModelApi,
    organizationViewModel: OrganizationViewModel,
    studentViewModel: com.example.volunteerbridge.viewmodelApi.StudentViewModel,
    attendanceViewModel: AttendanceViewModel,
    participationViewModel: ParticipationViewModel
) {
    val context = LocalContext.current
    val networkObserver = remember { NetworkConnectivityObserver(context) }
    val isOnline by networkObserver.isConnected.collectAsStateWithLifecycle(initialValue = true)

    val authState by authViewModelApi.authState.collectAsState()
    var showSaveSessionDialog by remember { mutableStateOf(false) }

    val savedToken = when (authState) {
        is AuthState.Success -> (authState as AuthState.Success).token
        else -> TokenManager.getToken()
    }

    val userType: UserType = remember(authState) {
        when (authState) {
            is AuthState.Success -> (authState as AuthState.Success).userType
            else -> {
                val token = TokenManager.getToken()
                val savedTypeStr = TokenManager.getRole()
                if (!token.isNullOrEmpty()) {
                    when (savedTypeStr.uppercase()) {
                        "ADMIN" -> UserType.Admin
                        "ORGANIZATION" -> UserType.Organization
                        "VOLUNTEER" -> UserType.Volunteer
                        "SUPERVISOR" -> UserType.Supervisor
                        "LEADER" -> UserType.Leader
                        else -> UserType.Student
                    }
                } else {
                    UserType.Loading
                }
            }
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            if (TokenManager.getToken().isNullOrEmpty()) {
                showSaveSessionDialog = true
            }
        }
    }


    if (showSaveSessionDialog) {
        AlertDialog(
            onDismissRequest = { showSaveSessionDialog = false },
            title = { Text(text = "حفظ معلومات الحساب", fontWeight = FontWeight.Bold) },
            text = { Text(text = "هل ترغب في حفظ بيانات تسجيل الدخول لكي تدخل تلقائياً في المرات القادمة؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModelApi.saveSessionManually()
                        showSaveSessionDialog = false
                    }
                ) {
                    Text("نعم، احفظ", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveSessionDialog = false }) {
                    Text("ليس الآن")
                }
            }
        )
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(userType, savedToken) {
        if (!savedToken.isNullOrEmpty()) {
            when (userType) {
                is UserType.Admin -> {
                    adminViewModel.fetchPendingOrganizations()
                }
                is UserType.Organization -> {
                    organizationViewModel.fetchMyOrganization()
                }
                is UserType.Student, is UserType.Volunteer -> {
                    Log.d("Adsa", "HomeScreen: sadasdas")
                    studentViewModel.fetchCurrentStudentProfile()
                }
                else -> {}
            }
        }
    }

    Scaffold(
        bottomBar = {
            val rootScreens = listOf(
                Destination.Student.Home.route,
                Destination.Student.MyTasks.route,
                Destination.Student.AllOpp.route,
                Destination.Student.Profile.route,
                Destination.Organization.Dashboard.route,
                Destination.Organization.Manage.route,
                Destination.Organization.Applications.route,
                Destination.Organization.Profile.route
            )

            val shouldShowBottomBar = currentRoute in rootScreens && (userType !is UserType.Admin)

            if (shouldShowBottomBar) {
                when (userType) {
                    is UserType.Student, is UserType.Volunteer -> {
                        val studentScreens = listOf(
                            Destination.Student.Home,
                            Destination.Student.MyTasks,
                            Destination.Student.AllOpp,
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
            if (!isOnline) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "لا يوجد اتصال بالإنترنت. يرجى التحقق من الشبكة.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            when (userType) {
                is UserType.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is UserType.Admin -> {
                    AdminDashboardScreen(
                        authViewModelApi = authViewModelApi,
                        adminViewModelApi = adminViewModel,
                        onLogoutSuccess = {
                            authViewModelApi.logout()
                            rootNavController.navigate(Screen.LoginScreen.rout) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
                is UserType.Organization, is UserType.Student, is UserType.Volunteer -> {
                    AppNavHost(
                        navController = navController,
                        rootNavController = rootNavController,
                        userType = userType,
                        notViewModel = notViewModel,
                        activityViewModel = activityViewModel,
                        authViewModelApi = authViewModelApi,
                        organizationViewModel = organizationViewModel,
                        studentViewModel = studentViewModel,
                        participationViewModel = participationViewModel,
                        attendanceViewModel = attendanceViewModel,
                        token = savedToken
                    )
                }
                is UserType.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Failed to load data.", color = MaterialTheme.colorScheme.error)
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