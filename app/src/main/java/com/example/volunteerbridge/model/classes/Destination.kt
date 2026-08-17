package com.example.volunteerbridge.model.classes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.volunteerbridge.model.classes.Destination.Organization

sealed class Destination(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    sealed class Student(route: String, icon: ImageVector, label: String) :
        Destination(route, icon, label) {
        object Home : Student("stu_home", Icons.Default.Home, "Home")
        object AllOpp : Student("all_opportunities_screen", Icons.Default.Notifications, "AllOpp")
        object MyTasks : Student("stu_tasks", Icons.Default.DateRange, "My Tasks")
        object Profile : Student("stu_profile", Icons.Default.Person, "Profile")
    }

    sealed class Organization(route: String, icon: ImageVector, label: String) :
        Destination(route, icon, label) {
        object Dashboard : Organization("org_home", Icons.Default.Home, "Dashboard")
        object Manage : Organization("org_manage", Icons.Default.List, "Manage")
        object Applications : Organization("org_app", Icons.Default.Notifications, "Applications")
        object Profile : Organization("org_profile", Icons.Default.Person, "Profile")
    }
}

sealed class SubClasses(val route: String) {
    sealed class SubClassesStu(route: String) : SubClasses(route) {
        object OppDetail : SubClasses("opp_detail")
        object Notification : SubClassesStu("notification")
    }
    sealed class SubClassesOrg(route: String) : SubClasses(route) {
        object Notification : SubClassesOrg("org_not")
        object OpportunityStats : SubClassesOrg("opportunity_stats")
        object OpportunityEdit : SubClassesOrg("opportunity_edit")
        object EditeProfile : SubClassesOrg("edit_profile")
        object ManageVolunteers : SubClassesOrg("manage_volunteers")
        object CreateOpportunity : SubClassesOrg("create_opportunity_route")
    }
}