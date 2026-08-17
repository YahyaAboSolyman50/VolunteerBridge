package com.example.volunteerbridge.view.nav_bottom.org.home

import android.icu.util.Calendar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.data.model.request.ActivityRequest
import com.example.volunteerbridge.viewmodelApi.ActivityViewModel
import com.valentinilk.shimmer.shimmer
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOpportunityScreen(
    activityId: Int,
    activityViewModel: ActivityViewModel,
    token: String,
    onBackClick: () -> Unit
) {
    // تعريف حالات الحقول
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var registrationDeadline by remember { mutableStateOf("") }
    var volunteerLimit by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("active") }
    var hours by remember { mutableStateOf("") }

    // حالة التحميل الخاصة بجلب بيانات النشاط لأول مرة
    var isLoading by remember { mutableStateOf(true) }

    // مراقبة حالة التحميل والتحديث من الـ ViewModel
    val isUpdating by activityViewModel.isUpdating.collectAsState()

    // حالات التحكم في إظهار مربعات اختيار التاريخ (DatePicker Dialogs)
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showDeadlinePicker by remember { mutableStateOf(false) }

    // جلب بيانات النشاط المحدد مباشرة من السيرفر عند فتح الشاشة
    LaunchedEffect(activityId) {
        isLoading = true
        activityViewModel.getActivityById(activityId) { activity ->
            title = activity.title ?: ""
            description = activity.description ?: ""
            category = activity.category ?: ""
            location = activity.location ?: ""
            startDate = activity.startDate ?: ""
            endDate = activity.endDate ?: ""
            registrationDeadline = activity.registrationDeadline ?: ""
            volunteerLimit = activity.volunteerLimit?.toString() ?: ""
            status = activity.status ?: "active"
            hours = activity.hours?.toString() ?: ""
            isLoading = false
        }
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary

    // --- نافذة تقويم التاريخ البداية ---
    if (showStartDatePicker) {
        MyDatePickerDialog(
            onDateSelected = { startDate = it },
            onDismiss = { showStartDatePicker = false }
        )
    }

    // --- نافذة تقويم تاريخ النهاية ---
    if (showEndDatePicker) {
        MyDatePickerDialog(
            onDateSelected = { endDate = it },
            onDismiss = { showEndDatePicker = false }
        )
    }

    // --- نافذة تقويم موعد التسجيل ---
    if (showDeadlinePicker) {
        MyDatePickerDialog(
            onDateSelected = { registrationDeadline = it },
            onDismiss = { showDeadlinePicker = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_opportunity_title), fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isUpdating) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            EditOpportunityScreenShimmer(modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- بيانات النظام (للقراءة فقط) ---
                Text(
                    text = stringResource(R.string.system_info_read_only),
                    color = primaryColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReadOnlyBox(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.activity_id_label),
                        value = activityId.toString()
                    )
                    ReadOnlyBox(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.status_label),
                        value = status
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                // --- البيانات الأساسية ---
                Text(
                    text = stringResource(R.string.general_details_label),
                    color = primaryColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                EditField(label = stringResource(R.string.title_label), value = title) { title = it }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EditField(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.category_label),
                        value = category
                    ) { category = it }

                    EditField(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.location_label),
                        value = location
                    ) { location = it }
                }

                // --- التواريخ (تفتح نافذة التقويم) ---
                Text(
                    text = stringResource(R.string.schedule_deadlines_label),
                    color = primaryColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        DateField(
                            label = stringResource(R.string.start_date_label),
                            value = startDate,
                            onClick = { showStartDatePicker = true }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        DateField(
                            label = stringResource(R.string.end_date_label),
                            value = endDate,
                            onClick = { showEndDatePicker = true }
                        )
                    }
                }

                DateField(
                    label = stringResource(R.string.registration_deadline_label),
                    value = registrationDeadline,
                    onClick = { showDeadlinePicker = true }
                )

                // --- الحدود والساعات ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EditField(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.volunteer_limit_label),
                        value = volunteerLimit
                    ) { volunteerLimit = it }

                    EditField(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.hours_label),
                        value = hours
                    ) { hours = it }
                }

                // --- الوصف ---
                EditField(
                    label = stringResource(R.string.description_label),
                    value = description,
                    singleLine = false,
                    modifier = Modifier.height(120.dp)
                ) { description = it }

                Spacer(modifier = Modifier.height(16.dp))

                // --- زر تحديث البيانات ---
                Button(
                    onClick = {
                        val request = ActivityRequest(
                            title = title,
                            description = description,
                            category = category.ifBlank { null },
                            location = location,
                            startDate = startDate.ifBlank { null },
                            endDate = endDate.ifBlank { null },
                            registrationDeadline = registrationDeadline.ifBlank { null },
                            volunteerLimit = volunteerLimit.toLongOrNull(),
                            status = status.ifBlank { "active" },
                            hours = hours.toLongOrNull()
                        )

                        activityViewModel.updateActivity(activityId, request) {
                            onBackClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    enabled = !isUpdating
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.update_opportunity_button),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// --- دالة لعرض نافذة التقويم (DatePicker Dialog) وتنسيق التاريخ بصيغة YYYY-MM-DD ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = millis
                        }
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        onDateSelected(formatter.format(calendar.time))
                    }
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

// --- حقل مخصص للتواريخ يفتح التقويم عند النقر عليه ---
@Composable
fun DateField(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onClick() }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                enabled = false, // يمنع الكتابة اليدوية ويجبر المستخدم على استخدام التقويم
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.select_date),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun EditOpportunityScreenShimmer(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxSize()
            .shimmer()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(colorScheme.surfaceVariant)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorScheme.surfaceVariant)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorScheme.surfaceVariant)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorScheme.surfaceVariant)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorScheme.surfaceVariant)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorScheme.surfaceVariant)
        )
    }
}

@Composable
fun ReadOnlyBox(modifier: Modifier, label: String, value: String) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 11.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Text(
                text = value,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontSize = 13.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun EditField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}