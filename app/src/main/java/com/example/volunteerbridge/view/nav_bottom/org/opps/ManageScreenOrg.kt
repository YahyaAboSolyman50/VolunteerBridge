package com.example.volunteerbridge.view.nav_bottom.org.opps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.data.model.response.ActivityResponse
import com.example.volunteerbridge.viewmodelApi.ActivityViewModel
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageScreen(
    onBackClick: () -> Unit,
    onCreateOppClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onViewDetailOppClick: (String) -> Unit,
    activityViewModel: ActivityViewModel,
    userToken: String
) {
    val myActivities by activityViewModel.myActivities.collectAsState()
    val isLoading by activityViewModel.isLoading.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    // إعادة جلب الفرص الخاصة بالمؤسسة عند الدخول للشاشة
    LaunchedEffect(userToken) {
        if (userToken.isNotEmpty()) {
//          activityViewModel.loadMyOrganizationActivities(userToken)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.manage_opportunities_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back_content_description),
                            tint = colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateOppClick,
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_opportunity_content_description))
            }
        },
        containerColor = colorScheme.background
    ) { innerPadding ->
        myActivities?.let {
            ManageScreenDesign(
                modifier = Modifier.padding(innerPadding),
                isLoading = isLoading,
                oppList = it,
                onEditClick = onEditClick,
                onViewDetailOppClick = onViewDetailOppClick
            )
        }
    }
}

@Composable
fun ManageScreenDesign(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    oppList: List<ActivityResponse>,
    onEditClick: (String) -> Unit,
    onViewDetailOppClick: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        if (isLoading && oppList.isEmpty()) {
            // استخدام الشيمر الهيكلي بدلاً من مؤشر التحميل الدائري المعتاد لتناسق التصميم
            ManageScreenShimmer(modifier = Modifier.fillMaxSize())
        } else if (oppList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_opportunities_posted),
                    color = colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = oppList,
                    key = { item -> item.id ?: item.hashCode() }
                ) { opportunity ->
                    OrgManageCard(
                        opportunity = opportunity,
                        onEditClick = { opportunity.id?.let { onEditClick(it.toString()) } },
                        onViewDetailOppClick = { opportunity.id?.let { onViewDetailOppClick(it.toString()) } }
                    )
                }
            }
        }
    }
}

@Composable
fun ManageScreenShimmer(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    LazyColumn(
        modifier = modifier
            .padding(20.dp)
            .shimmer(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        userScrollEnabled = false
    ) {
        items(4) {
            Card(
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // عنوان وهمي
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(18.dp)
                            .background(colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                    )
                    // وصف وهمي
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                    )
                    // أزرار وهمية بالأفل
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(36.dp)
                                .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        )
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(36.dp)
                                .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrgManageCard(
    opportunity: ActivityResponse,
    onEditClick: () -> Unit,
    onViewDetailOppClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = opportunity.title.orEmpty(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Text(
                text = opportunity.description.orEmpty(),
                fontSize = 13.sp,
                color = colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.3f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.primary)
                ) {
                    Text(stringResource(R.string.edit_button), fontSize = 13.sp)
                }

                Button(
                    onClick = onViewDetailOppClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                ) {
                    Text(stringResource(R.string.details_button), fontSize = 13.sp, color = colorScheme.onPrimary)
                }
            }
        }
    }
}