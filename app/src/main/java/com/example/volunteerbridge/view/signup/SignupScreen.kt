package com.example.volunteerbridge.view.signup

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.model.classes.Organization
import com.example.volunteerbridge.model.classes.status.AuthState
import com.example.volunteerbridge.screens.Screen
import com.example.volunteerbridge.viewmodel.AuthViewModel

@Composable
fun SignupScreen(viewModel: AuthViewModel, navController: NavController) {
    OrganizationSignupScreen(viewModel, navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationSignupScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val orgName = remember { mutableStateOf("") }
    val emailOrg = remember { mutableStateOf("") }
    val licenseNum = remember { mutableStateOf("") }
    val phoneNum = remember { mutableStateOf("") }
    val passwordOrg = remember { mutableStateOf("") }
    val confirmPasswordOrg = remember { mutableStateOf("") }
    val descriptionOrg = remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val orgTypes = listOf("Local NGO", "Governmental", "International")
    var selectedType by remember { mutableStateOf(orgTypes[0]) }

    val authState by viewModel.authState.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            Toast.makeText(context, "Account Created Successfully", Toast.LENGTH_SHORT).show()
            viewModel.resetAuthState()
            navController.navigate(Screen.LoginScreen.rout) {
                popUpTo(Screen.SignupScreen.rout) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // العنوان
            Text(
                text = "Join Volunteer Bridge",
                color = colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Register your organization to start",
                color = colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // الحقول النصية (استخدام نفس تصميم صفحة اللوجن)
            SignupInputField(
                label = "Organization Name",
                value = orgName.value,
                onValueChange = { orgName.value = it },
                placeholder = "Full Name",
                isError = errors.nameError != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            SignupInputField(
                label = "Email Address",
                value = emailOrg.value,
                onValueChange = { emailOrg.value = it },
                placeholder = "org@example.com",
                isError = errors.emailError != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    SignupInputField(
                        label = "License Number",
                        value = licenseNum.value,
                        onValueChange = { licenseNum.value = it },
                        placeholder = "123-ABC",
                        isError = errors.licenseError != null
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    SignupInputField(
                        label = "Phone Number",
                        value = phoneNum.value,
                        onValueChange = { phoneNum.value = it },
                        placeholder = "059xxxxxx",
                        isError = errors.phoneError != null
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // نوع المنظمة (Dropdown)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Organization Type",
                    color = colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.surface,
                            unfocusedContainerColor = colorScheme.surface,
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.2f),
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(colorScheme.surface)
                    ) {
                        orgTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type, color = colorScheme.onSurface) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SignupInputField(
                label = "Password",
                value = passwordOrg.value,
                onValueChange = { passwordOrg.value = it },
                placeholder = "••••••••",
                isPassword = true,
                isError = errors.passwordError != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            SignupInputField(
                label = "Confirm Password",
                value = confirmPasswordOrg.value,
                onValueChange = { confirmPasswordOrg.value = it },
                placeholder = "••••••••",
                isPassword = true,
                isError = errors.confirmPasswordError != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            SignupInputField(
                label = "Description",
                value = descriptionOrg.value,
                onValueChange = { descriptionOrg.value = it },
                placeholder = "About the organization...",
                singleLine = false
            )



            Spacer(modifier = Modifier.height(40.dp))

            // الزر الرئيسي
            Button(
                onClick = {
                    viewModel.registerOrganization(
                        Organization(
                            nameOrg = orgName.value, emailOrg = emailOrg.value,
                            passwordOrg = passwordOrg.value, license = licenseNum.value,
                            phone = phoneNum.value, orgType = selectedType,
                            description = descriptionOrg.value,
                        ), confirmPasswordOrg.value
                    )
                },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = colorScheme.onPrimary)
                } else {
                    Text("Submit Request", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Account will be reviewed by admin",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onBackground.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SignupInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    isError: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme
    var passwordVisible by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = colorScheme.onSurface.copy(alpha = 0.4f)) },
            singleLine = singleLine,
            isError = isError,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,            trailingIcon = {
                if (isPassword) {
                    val image = if (passwordVisible)
                        R.drawable.view
                    else R.drawable.close_eye

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(image),
                            contentDescription = description,
                            tint = colorScheme.primary
                        )
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface,
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.2f),
                focusedContainerColor = colorScheme.surface,
                unfocusedContainerColor = colorScheme.surface,
                errorBorderColor = colorScheme.error
            )
        )
    }
}