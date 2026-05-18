package com.example.volunteerbridge.view.nav_bottom.org.home


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volunteerbridge.viewmodel.OpportunityViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOpportunityScreen(
    oppId: String,
    oppViewModel: OpportunityViewModel,
    onBackClick: () -> Unit
) {
    // جلب قائمة الفرص والبحث عن الفرصة المطلوبة
    val oppList by oppViewModel.orgOpp
    val opp = remember(oppList, oppId) { oppList.find { it.id == oppId } }

    // تعريف حالات الحقول (State) والقيم الابتدائية من الفرصة الحالية
    var title by remember { mutableStateOf(opp?.title ?: "") }
    var category by remember { mutableStateOf(opp?.category ?: "") }
    var description by remember { mutableStateOf(opp?.description ?: "") }
    var location by remember { mutableStateOf(opp?.location ?: "") }
    var vacancies by remember { mutableStateOf(opp?.vacancies?.toString() ?: "") }
    var requiredHours by remember { mutableStateOf(opp?.requiredHours?.toString() ?: "") }


    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Opportunity", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- بيانات النظام (للقراءة فقط) ---
            Text(
                text = "System Information (Read Only)",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReadOnlyBox(
                    modifier = Modifier.weight(1.2f),
                    label = "Opportunity ID",
                    value = opp?.id ?: "-"
                )
                ReadOnlyBox(
                    modifier = Modifier.weight(0.8f),
                    label = "Current Applicants",
                    value = "${opp?.applicantsCount ?: 0}"
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

            // --- بيانات أساسية (قابلة للتعديل) ---
            Text(
                text = "General Details",
                color = primaryColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            EditField(label = "Title", value = title) { title = it }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditField(
                    modifier = Modifier.weight(1f),
                    label = "Category",
                    value = category
                ) { category = it }

                EditField(
                    modifier = Modifier.weight(1f),
                    label = "Location",
                    value = location
                ) { location = it }
            }

            // --- الأرقام والكميات ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditField(
                    modifier = Modifier.weight(1f),
                    label = "Vacancies",
                    value = vacancies
                ) { vacancies = it }

                EditField(
                    modifier = Modifier.weight(1f),
                    label = "Required Hours",
                    value = requiredHours
                ) { requiredHours = it }
            }

            // --- الوصف (نص طويل) ---
            EditField(
                label = "Description",
                value = description,
                singleLine = false,
                modifier = Modifier.height(120.dp)
            ) { description = it }

            Spacer(modifier = Modifier.height(16.dp))

            // --- زر تحديث البيانات ---
            Button(
                onClick = {
                    val updates = mapOf(
                        "title" to title,
                        "category" to category,
                        "description" to description,
                        "location" to location,
                        "vacancies" to (vacancies.toIntOrNull() ?: 0),
                        "requiredHours" to (requiredHours.toIntOrNull() ?: 0)
                    )
                    oppViewModel.updateOpportunity(oppId, updates)
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text(
                    text = "Update Opportunity",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * مكون لعرض البيانات التي لا يمكن تعديلها
 */
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
            // يستخدم surface (رمادي فاتح في اللايت، أزرق داكن في الدارك)
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