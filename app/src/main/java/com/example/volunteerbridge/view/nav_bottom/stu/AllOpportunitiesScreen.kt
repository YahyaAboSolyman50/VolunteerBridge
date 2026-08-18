package com.example.volunteerbridge.view.nav_bottom.stu

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.model.classes.OpportunityConstants
import com.example.volunteerbridge.model.classes.SubClasses
import com.example.volunteerbridge.model.classes.NotificationModel
import com.example.volunteerbridge.view.nav_bottom.stu.home.OpportunityCard
import com.example.volunteerbridge.viewmodelApi.ActivityViewModel
import com.example.volunteerbridge.viewmodelApi.AuthViewModelApi
import com.example.volunteerbridge.viewmodelApi.NotViewModel
import com.example.volunteerbridge.viewmodelApi.StudentViewModel

// شاشة عرض جميع الفرص التطوعية المتاحة للطلاب مع إمكانية البحث والتصفية
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllOpportunitiesScreen(
    activityViewModel: ActivityViewModel,
    authViewModelApi: AuthViewModelApi,
    studentViewModel: StudentViewModel, // أضفناه هنا لجلب ساعات الطالب وبياناته
    notViewModel: NotViewModel,          // أضفناه هنا لإرسال الإشعارات عند التقديم
    navController: NavController
) {
    val context = LocalContext.current
    val userProfile by studentViewModel.currentUserData

    val activityList by activityViewModel.activities.collectAsState(initial = emptyList())
    val isJoinLoading by activityViewModel.isJoinLoading.collectAsState(initial = false)
    val selectedActivityId by activityViewModel.selectedActivityId.collectAsState(initial = null)

    // حساب الساعات الحالية للطالب
    val currentHours = userProfile?.totalCompletedHours?.toIntOrNull() ?: 0

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All") }
    var selectedType by remember { mutableStateOf("All") }
    var selectedCategory by remember { mutableStateOf("All") }

    // تحميل المشاركات والأنشطة عند فتح الشاشة للتأكد من تحديث حالة الأزرار
    LaunchedEffect(Unit) {
        activityViewModel.loadMyParticipations()
        activityViewModel.loadActivities()
    }

    // تصفية الفرص بناءً على البحث، الحالة، النوع، والتصنيف
    val filteredActivities = remember(searchQuery, selectedStatus, selectedType, selectedCategory, activityList) {
        activityList?.filter { activity ->
            val matchesSearch = searchQuery.isEmpty() ||
                    (activity.title?.contains(searchQuery, ignoreCase = true) == true ||
                            activity.description?.contains(searchQuery, ignoreCase = true) == true)

            val matchesStatus = selectedStatus.equals("All", ignoreCase = true) ||
                    (activity.status?.trim().equals(selectedStatus, ignoreCase = true))

            val matchesCategory = selectedCategory.equals("All", ignoreCase = true) ||
                    activity.category?.trim().equals(selectedCategory, ignoreCase = true)

            matchesSearch && matchesStatus && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.all_opportunities), fontWeight = FontWeight.Bold) },
                navigationIcon = {}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // شريط البحث
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(R.string.search_all_opportunities)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // شريط الفلاتر الأفقية
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                item {
                    FilterDropdown(
                        label = stringResource(R.string.filter_status),
                        options = listOf("All") + OpportunityConstants.statuses,
                        selectedOption = selectedStatus,
                        onOptionSelected = { selectedStatus = it }
                    )
                }
                item {
                    FilterDropdown(
                        label = stringResource(R.string.filter_type),
                        options = listOf("All") + OpportunityConstants.orgTypes,
                        selectedOption = selectedType,
                        onOptionSelected = { selectedType = it }
                    )
                }
                item {
                    FilterDropdown(
                        label = stringResource(R.string.filter_category),
                        options = listOf("All") + OpportunityConstants.categories,
                        selectedOption = selectedCategory,
                        onOptionSelected = { selectedCategory = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // قائمة الفرص الكاملة والمصفاة
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (filteredActivities.isNullOrEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_opportunities_found),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filteredActivities!!.size) { index ->
                        val opp = filteredActivities!![index]
                        val isThisCardLoading = isJoinLoading && selectedActivityId == opp.id

                        OpportunityCard(
                            opp = opp,
                            isThisButtonLoading = isThisCardLoading,
                            activityViewModel = activityViewModel,
                            currentUserHours = currentHours, // تمرير الساعات الحالية للتحقق من شرط الـ 150 ساعة
                            onCardClick = { id ->
                                activityViewModel.selectActivity(opp)
                                navController.navigate("${SubClasses.SubClassesStu.OppDetail.route}/$id")
                            },
                            onApplyClick = {
                                opp.id?.let { id ->
                                    activityViewModel.joinActivity(activityId = id) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.successfully_joined_opportunity),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val orgId = opp.organization
                                        val studentId = userProfile?.id
                                        val oppTitle = opp.title ?: ""

                                        if (orgId != null && orgId != 0) {
                                            val notification = NotificationModel(
                                                receiverId = orgId,
                                                senderId = studentId ?: 0,
                                                title = context.getString(R.string.new_join_request),
                                                message = context.getString(R.string.student_joined_opportunity_msg, oppTitle),
                                                type = "APPLICATION"
                                            )
                                            notViewModel.sendNotification(
                                                notification,
                                                {
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.notification_sent_to_org),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                {
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.failed_to_send_notification),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        }
                                    }
                                }
                            },
                            context = context
                        )
                    }
                }
            }
        }
    }
}

/**
 * مكون القائمة المنسدلة المخصص لفلاتر البحث.
 */
@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(4.dp)) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .background(MaterialTheme.colorScheme.surfaceVariant),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$label: $selectedOption", style = MaterialTheme.typography.bodySmall)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}