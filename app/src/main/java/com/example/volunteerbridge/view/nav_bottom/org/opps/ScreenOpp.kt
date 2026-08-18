package com.example.volunteerbridge.view.nav_bottom.org.opps

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.data.model.OpportunityCategory
import com.example.volunteerbridge.data.model.request.ActivityRequest
import com.example.volunteerbridge.viewmodelApi.ActivityViewModel
import com.valentinilk.shimmer.shimmer
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOppScreen(
    onBackClick: () -> Unit,
    activityViewModel: ActivityViewModel,
    userToken: String
) {
    val context = LocalContext.current
    val isCreating by activityViewModel.isCreating.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val defaultCategoryLabel = stringResource(R.string.select_category)
    var selectedCategoryLabel by remember { mutableStateOf(defaultCategoryLabel) }
    var expandedCategory by remember { mutableStateOf(false) }

    var volunteerLimit by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }
    var locationName by remember { mutableStateOf("") }

    // الحقول الخاصة بالتواريخ والحالة
    var startDate by remember { mutableStateOf("2026-06-01") }
    var endDate by remember { mutableStateOf("2026-06-10") }
    var registrationDeadline by remember { mutableStateOf("2026-05-28") }

    val status by remember { mutableStateOf("active") }

    // دالة إظهار نافذة اختيار التاريخ (DatePickerDialog)
    fun showDatePicker(initialDate: String, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val parts = initialDate.split("-")
        if (parts.size == 3) {
            parts[0].toIntOrNull()?.let { calendar.set(Calendar.YEAR, it) }
            parts[1].toIntOrNull()?.let { calendar.set(Calendar.MONTH, it - 1) }
            parts[2].toIntOrNull()?.let { calendar.set(Calendar.DAY_OF_MONTH, it) }
        }

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedMonth = String.format("%02d", month + 1)
                val formattedDay = String.format("%02d", dayOfMonth)
                onDateSelected("$year-$formattedMonth-$formattedDay")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.create_opportunity_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        containerColor = colorScheme.background
    ) { innerPadding ->
        if (isCreating) {
            CreateOppScreenShimmer(modifier = Modifier.padding(innerPadding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // كارد المعلومات الأساسية
                Card(
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.basic_information),
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            fontSize = 15.sp
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(stringResource(R.string.opportunity_title_label)) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(stringResource(R.string.detailed_description_label)) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedCategoryLabel,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.category_label)) },
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { expandedCategory = true }
                            )
                            DropdownMenu(
                                expanded = expandedCategory,
                                onDismissRequest = { expandedCategory = false }
                            ) {
                                OpportunityCategory.labels.forEach { label ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            selectedCategoryLabel = label
                                            expandedCategory = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // كارد المكان والساعات والحد الأقصى
                Card(
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.location_hours_limit_section),
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            fontSize = 15.sp
                        )

                        OutlinedTextField(
                            value = locationName,
                            onValueChange = { locationName = it },
                            label = { Text(stringResource(R.string.location_address_label)) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = volunteerLimit,
                            onValueChange = { volunteerLimit = it },
                            label = { Text(stringResource(R.string.volunteer_limit_label)) },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = hours,
                            onValueChange = { hours = it },
                            label = { Text(stringResource(R.string.volunteer_hours_label)) },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // كارد التواريخ والحالة (مدعوم بنافذة اختيار التاريخ DatePickerDialog)
                Card(
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.dates_and_status_section),
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary,
                            fontSize = 15.sp
                        )

                        // حقل تاريخ البداية
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = startDate,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.start_date_label)) },
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showDatePicker(startDate) { startDate = it } }
                            )
                        }

                        // حقل تاريخ النهاية
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = endDate,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.end_date_label)) },
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showDatePicker(endDate) { endDate = it } }
                            )
                        }

                        // حقل الموعد النهائي للتسجيل
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = registrationDeadline,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.registration_deadline_label)) },
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        showDatePicker(registrationDeadline) {
                                            registrationDeadline = it
                                        }
                                    }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val fillFieldsErrorMsg = stringResource(R.string.fill_all_fields_error)
                val successPublishMsg = stringResource(R.string.opportunity_published_success)

                // زر النشر مع منطق التحقق من التواريخ
                Button(
                    onClick = {
                        val limit = volunteerLimit.toIntOrNull()
                        val totalHours = hours.toIntOrNull()
                        val categoryBackendKey = OpportunityCategory.fromLabel(selectedCategoryLabel)
                        Log.d("Asasas", "CreateOppScreen: $categoryBackendKey")

                        // 1. التحقق من الحقول الفارغة
                        if (title.isBlank() || description.isBlank() || locationName.isBlank() || categoryBackendKey == null) {
                            Toast.makeText(context, fillFieldsErrorMsg, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // 2. التحقق من أن تاريخ البداية يسبق تاريخ النهاية
                        if (startDate >= endDate) {
                            Toast.makeText(context, "تاريخ البداية يجب أن يكون قبل تاريخ النهاية", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // 3. التحقق من أن الموعد النهائي للتسجيل يسبق تاريخ البداية أو يساويه
                        if (registrationDeadline > startDate) {
                            Toast.makeText(context, "الموعد النهائي للتسجيل يجب أن يكون قبل أو يوافق تاريخ البداية", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val request = ActivityRequest(
                            title = title,
                            description = description,
                            category = categoryBackendKey,
                            location = locationName,
                            startDate = startDate,
                            endDate = endDate,
                            registrationDeadline = registrationDeadline,
                            volunteerLimit = limit,
                            status = status,
                            hours = totalHours
                        )

                        activityViewModel.createOpportunity(
                            request = request,
                            onSuccess = {
                                Toast.makeText(context, successPublishMsg, Toast.LENGTH_SHORT).show()
                                onBackClick()
                            }
                        )
                    },
                    enabled = !isCreating,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                ) {
                    Text(
                        text = stringResource(R.string.publish_opportunity_button),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun CreateOppScreenShimmer(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(20.dp)
            .shimmer(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // شيمر الكارد الأول
        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colorScheme.surfaceVariant.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier
                    .width(120.dp)
                    .height(16.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(4.dp)))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp)))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp)))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp)))
            }
        }

        // شيمر الكارد الثاني
        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colorScheme.surfaceVariant.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier
                    .width(150.dp)
                    .height(16.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(4.dp)))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp)))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp)))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp)))
            }
        }

        // شيمر الكارد الثالث (التواريخ والحالة)
        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colorScheme.surfaceVariant.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier
                    .width(140.dp)
                    .height(16.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(4.dp)))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp)))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp)))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp)))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(colorScheme.surfaceVariant, RoundedCornerShape(12.dp)))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
        )
    }
}