package com.example.volunteerbridge.view.nav_bottom.org.profile


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.volunteerbridge.model.classes.Organization
import com.example.volunteerbridge.viewmodel.AuthViewModel
import com.example.volunteerbridge.viewmodel.OrgViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrgProfileScreen(
    navController: NavController,
    orgViewModel: OrgViewModel
) {
    // جلب بيانات المنظمة الحالية من الـ ViewModel
    val currentOrg = orgViewModel.currentOrgData.value ?: Organization()

    // حالات الحقول القابلة للتعديل باستخدام remember لضمان بقاء الحالة أثناء إعادة التكوين
    var name by remember { mutableStateOf(currentOrg.nameOrg) }
    var phone by remember { mutableStateOf(currentOrg.phone) }
    var orgType by remember { mutableStateOf(currentOrg.orgType) }
    var description by remember { mutableStateOf(currentOrg.description) }
    var license by remember { mutableStateOf(currentOrg.license) }

    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // حقل الاسم
            EditField(label = "Organization Name", value = name, onValueChange = { name = it })

            Spacer(modifier = Modifier.height(16.dp))

            // حقل الهاتف
            EditField(label = "Phone Number", value = phone, onValueChange = { phone = it })

            Spacer(modifier = Modifier.height(16.dp))

            // حقل رقم الترخيص
            EditField(label = "License Number", value = license, onValueChange = { license = it })

            Spacer(modifier = Modifier.height(16.dp))

            // حقل الوصف (متعدد الأسطر)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // زر الحفظ
            Button(
                onClick = {
                    val updatedOrg = currentOrg.copy(
                        nameOrg = name,
                        phone = phone,
                        description = description,
                        license = license,
                        orgType = orgType
                    )
                    orgViewModel.updateOrganizationData(updatedOrg)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary
                )
            ) {
                Text(
                    "Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onPrimary
                )
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
            unfocusedBorderColor = colorScheme.outline
        )
    )
}