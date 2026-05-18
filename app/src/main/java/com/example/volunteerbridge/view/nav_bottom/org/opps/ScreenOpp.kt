package com.example.volunteerbridge.view.nav_bottom.org.opps

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.model.classes.OpportunityModel
import com.example.volunteerbridge.model.classes.status.UiState
import com.example.volunteerbridge.viewmodel.OpportunityViewModel
import com.example.volunteerbridge.viewmodel.OrgViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateOppScreen(
    oppViewModel: OpportunityViewModel,
    orgViewModel: OrgViewModel,
    onSuccess: () -> Unit,
    onBackClick: () -> Unit,
) {
    val currentOrg by orgViewModel.currentOrgData
    val uiState by oppViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    // --- الحالات الأساسية ---
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Social Work") }
    var location by remember { mutableStateOf("") }
    var requiredHours by remember { mutableStateOf("") }
    var vacancies by remember { mutableStateOf("") }
    var startDate by remember { mutableLongStateOf(0L) }
    var endDate by remember { mutableLongStateOf(0L) }
    var deadline by remember { mutableLongStateOf(0L) }

    // --- القوائم ---
    var taskInput by remember { mutableStateOf("") }
    val tasksList = remember { mutableStateListOf<String>() }
    var requirementInput by remember { mutableStateOf("") }
    val requirementsList = remember { mutableStateListOf<String>() }
    var tagInput by remember { mutableStateOf("") }
    val tagsList = remember { mutableStateListOf<String>() }

    var expandedCat by remember { mutableStateOf(false) }
    val categories = listOf("Technical", "Medical", "Educational", "Administrative", "Social Work", "Other")

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(colorScheme.primary.copy(alpha = 0.05f), colorScheme.background)
    )

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            Toast.makeText(context, "Opportunity Posted!", Toast.LENGTH_SHORT).show()
            onSuccess()
            oppViewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post New Opportunity", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background)
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. المعلومات الأساسية
                Card(
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CustomTextField(value = title, onValueChange = { title = it }, label = "Opportunity Title", icon = Icons.Default.Edit)
                        CustomTextField(value = description, onValueChange = { description = it }, label = "Detailed Description", icon = Icons.Default.Info, minLines = 3)

                        ExposedDropdownMenuBox(expanded = expandedCat, onExpandedChange = { expandedCat = !expandedCat }) {
                            OutlinedTextField(
                                value = category, onValueChange = {}, readOnly = true,
                                label = { Text("Select Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                                categories.forEach { selection ->
                                    DropdownMenuItem(text = { Text(selection) }, onClick = { category = selection; expandedCat = false })
                                }
                            }
                        }
                    }
                }

                // 2. الأرقام والموقع
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CustomTextField(value = requiredHours, onValueChange = { if (it.all { c -> c.isDigit() }) requiredHours = it }, label = "Hours", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                    CustomTextField(value = vacancies, onValueChange = { if (it.all { c -> c.isDigit() }) vacancies = it }, label = "Seats", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                }
                CustomTextField(value = location, onValueChange = { location = it }, label = "Location (e.g. Remote, Gaza)", icon = Icons.Default.LocationOn)

                // 3. نظام المهام (Checklist)
                ListManagerSection(
                    label = "Opportunity Tasks (Checklist)",
                    inputValue = taskInput,
                    onInputChange = { taskInput = it },
                    list = tasksList,
                    onAdd = { if (taskInput.isNotBlank()) { tasksList.add(taskInput); taskInput = "" } },
                    onRemove = { tasksList.remove(it) },
                    icon = Icons.Default.CheckCircle,
                    accentColor = Color(0xFF4CAF50)
                )

                // 4. التواريخ
                Text("Timing & Deadlines", fontWeight = FontWeight.Bold, color = colorScheme.primary)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DatePickerField("Start Date", startDate, Modifier.weight(1f)) { startDate = it }
                        DatePickerField("End Date", endDate, Modifier.weight(1f)) { endDate = it }
                    }
                    DatePickerField("Application Deadline", deadline, Modifier.fillMaxWidth()) { deadline = it }
                }

                // 5. المتطلبات والوسوم
                ListManagerSection(
                    label = "Requirements",
                    inputValue = requirementInput,
                    onInputChange = { requirementInput = it },
                    list = requirementsList,
                    onAdd = { if (requirementInput.isNotBlank()) { requirementsList.add(requirementInput); requirementInput = "" } },
                    onRemove = { requirementsList.remove(it) }
                )

                ListManagerSection(
                    label = "Tags",
                    inputValue = tagInput,
                    onInputChange = { tagInput = it },
                    list = tagsList,
                    onAdd = { if (tagInput.isNotBlank()) { tagsList.add(tagInput); tagInput = "" } },
                    onRemove = { tagsList.remove(it) },
                    isTag = true
                )

                // 6. زر النشر
                Button(
                    onClick = {
                        val newOpp = OpportunityModel(
                            orgId = currentOrg?.uid ?: "",
                            orgName = currentOrg?.nameOrg ?: "",
                            title = title,
                            description = description,
                            category = category,
                            location = location,
                            requiredHours = requiredHours.toIntOrNull() ?: 0,
                            vacancies = vacancies.toIntOrNull() ?: 0,
                            startDate = startDate,
                            endDate = endDate,
                            deadline = deadline,
                            requirements = requirementsList.toList(),
                            tags = tagsList.toList(),
                            tasks = tasksList.toList(),
                            status = "Active"
                        )
                        oppViewModel.uploadOpportunity(newOpp)
                    },
                    modifier = Modifier.fillMaxWidth().height(58.dp).padding(bottom = 10.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                    enabled = uiState !is UiState.Loading && title.isNotEmpty()
                ) {
                    if (uiState is UiState.Loading) {
                        CircularProgressIndicator(color = colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Confirm and Post", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// --- المكونات المساعدة النظيفة بقيت بدون تعديل لمنع أخطاء الإستدعاء ---

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListManagerSection(
    label: String,
    inputValue: String,
    onInputChange: (String) -> Unit,
    list: List<String>,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
    isTag: Boolean = false,
    icon: ImageVector = Icons.Default.AddCircle,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = onInputChange,
                placeholder = { Text("Add...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            )
            IconButton(onClick = onAdd) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(35.dp))
            }
        }
        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            list.forEach { item ->
                AssistChip(
                    onClick = { onRemove(item) },
                    label = { Text(if (isTag) "#$item" else item) },
                    trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(14.dp)) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = icon?.let { { Icon(it, contentDescription = null) } },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        minLines = minLines
    )
}

@Composable
fun DatePickerField(label: String, timestamp: Long, modifier: Modifier = Modifier, onDateSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val displayText = if (timestamp == 0L) label else sdf.format(Date(timestamp))

    OutlinedCard(
        onClick = {
            val cal = Calendar.getInstance()
            DatePickerDialog(context, { _, y, m, d ->
                cal.set(y, m, d)
                onDateSelected(cal.timeInMillis)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        },
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(Modifier.fillMaxSize().padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, tint = MaterialTheme.colorScheme.primary, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(displayText, fontSize = 14.sp)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListManagerSection(
    label: String,
    inputValue: String,
    onInputChange: (String) -> Unit,
    list: List<String>,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
    isTag: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = onInputChange,
                placeholder = { Text("Add ${if (isTag) "tag" else "requirement"}") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            IconButton(onClick = onAdd) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFF042A63), modifier = Modifier.size(32.dp))
            }
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            list.forEach { item ->
                InputChip(
                    selected = true,
                    onClick = { onRemove(item) },
                    label = { Text(if (isTag) "#$item" else item) },
                    trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(14.dp)) }
                )
            }
        }
    }
}