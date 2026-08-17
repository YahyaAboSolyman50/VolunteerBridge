package com.example.volunteerbridge.view.nav_bottom.org.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.data.model.OrganizationCategory
import com.example.volunteerbridge.data.model.request.OrganizationRequest
import com.example.volunteerbridge.viewmodelApi.OrganizationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrgProfileScreen(
    navController: NavController,
    organizationViewModel: OrganizationViewModel,
    token: String
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    val currentOrg by organizationViewModel.currentOrganization

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var orgType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var license by remember { mutableStateOf("") }

    var expandedCategory by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }

    LaunchedEffect(currentOrg) {
        currentOrg?.let {
            name = it.name ?: ""
            phone = it.phone ?: ""
            orgType = it.category ?: ""
            description = it.description ?: ""
            license = it.license ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.edit_org_profile_title),
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colorScheme.background)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentOrg == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            } else {
                EditField(label = stringResource(R.string.org_name_label), value = name, onValueChange = { name = it })
                EditField(label = stringResource(R.string.phone_number_label), value = phone, onValueChange = { phone = it })
                EditField(label = stringResource(R.string.license_number_label), value = license, onValueChange = { license = it })

                val selectCategoryHint = stringResource(R.string.select_category_hint)
                // حقل التصنيف المتوافق مع الـ Enum الصارم (ngo, government, international)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = orgType.ifBlank { selectCategoryHint },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.category_type_label)) },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.outline,
                            focusedLabelColor = colorScheme.primary,
                            unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface
                        )
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
                        // استخدام القائمة الصارمة المعرفة في الـ Enum
                        val categories = OrganizationCategory.list
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    orgType = category
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline,
                        focusedLabelColor = colorScheme.primary,
                        unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                        focusedTextColor = colorScheme.onSurface,
                        unfocusedTextColor = colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                val updateSuccessMsg = stringResource(R.string.update_success_msg)

                Button(
                    onClick = {
                        if (isUpdating || currentOrg == null) return@Button
                        isUpdating = true

                        val request = OrganizationRequest(
                            name = name,
                            phone = phone,
                            description = description,
                            license = license,
                            category = orgType
                        )

                        organizationViewModel.updateOrganization(
                            id = currentOrg?.id ?: 0,
                            org = request,
                            onSuccess = {
                                isUpdating = false
                                Toast.makeText(context, updateSuccessMsg, Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary
                    )
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(color = colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = stringResource(R.string.save_changes_button),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditField(label: String, value: String, onValueChange: (String) -> Unit) {
    val colorScheme = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline,
            focusedLabelColor = colorScheme.primary,
            unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onSurface
        )
    )
}