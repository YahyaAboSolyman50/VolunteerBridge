package com.example.volunteerbridge.view.signup

import android.widget.Toast
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
import com.example.volunteerbridge.data.model.request.OrganizationRequest
import com.example.volunteerbridge.model.AuthValidator
import com.example.volunteerbridge.model.classes.SignupErrors
import com.example.volunteerbridge.screens.Screen
import com.example.volunteerbridge.viewmodelApi.OrganizationViewModel

@Composable
fun SignupScreen(
    viewModel: OrganizationViewModel,
    navController: NavController
) {
    OrganizationSignupScreen(viewModel, navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationSignupScreen(
    viewModel: OrganizationViewModel,
    navController: NavController
) {
    val orgName = remember { mutableStateOf("") }
    val emailOrg = remember { mutableStateOf("") }
    val phoneNum = remember { mutableStateOf("") }
    val licenseOrg = remember { mutableStateOf("") }
    val passwordOrg = remember { mutableStateOf("") }
    val confirmPasswordOrg = remember { mutableStateOf("") }
    val descriptionOrg = remember { mutableStateOf("") }
    val addressOrg = remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    // 1. القائمة المعروضة في الواجهة للمستخدم
    val orgDisplayTypes = listOf("Local NGO", "Governmental", "International")
    var selectedType by remember { mutableStateOf(orgDisplayTypes[0]) }

    val categoryMapping = mapOf(
        "Local NGO" to "ngo",
        "Governmental" to "government",
        "International" to "international"
    )

    val isLoading by viewModel.isLoading
    var errors by remember { mutableStateOf(SignupErrors()) }

    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

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

            // حقل اسم المؤسسة
            SignupInputField(
                label = "Organization Name",
                value = orgName.value,
                onValueChange = { orgName.value = it },
                placeholder = "Full Name",
                isError = errors.nameError != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // حقل البريد الإلكتروني
            SignupInputField(
                label = "Email Address",
                value = emailOrg.value,
                onValueChange = { emailOrg.value = it },
                placeholder = "org@example.com",
                isError = errors.emailError != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // العنوان ورقم الهاتف في صف واحد
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    SignupInputField(
                        label = "Address",
                        value = addressOrg.value,
                        onValueChange = { addressOrg.value = it },
                        placeholder = "City, Country",
                        isError = false
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

            // حقل رقم الترخيص
            SignupInputField(
                label = "License Number",
                value = licenseOrg.value,
                onValueChange = { licenseOrg.value = it },
                placeholder = "Lic-XXXXXX",
                isError = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // نوع المنظمة (Dropdown لـ Category)
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
                        orgDisplayTypes.forEach { type ->
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

            // حقل كلمة المرور
            SignupInputField(
                label = "Password",
                value = passwordOrg.value,
                onValueChange = { passwordOrg.value = it },
                placeholder = "••••••••",
                isPassword = true,
                isError = errors.passwordError != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // حقل تأكيد كلمة المرور
            SignupInputField(
                label = "Confirm Password",
                value = confirmPasswordOrg.value,
                onValueChange = { confirmPasswordOrg.value = it },
                placeholder = "••••••••",
                isPassword = true,
                isError = errors.confirmPasswordError != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // حقل الوصف
            SignupInputField(
                label = "Description",
                value = descriptionOrg.value,
                onValueChange = { descriptionOrg.value = it },
                placeholder = "About the organization...",
                singleLine = false
            )

            Spacer(modifier = Modifier.height(40.dp))

            // الزر الرئيسي لإرسال البيانات
            Button(
                onClick = {
                    // تحويل النص المختار في الواجهة إلى القيمة المقبولة في السيرفر لـ category
                    val apiCategory = categoryMapping[selectedType] ?: "ngo"

                    // إنشاء الكائن المطابق تماماً لـ OrganizationRegister في الـ Swagger
                    val request = OrganizationRequest(
                        name = orgName.value,
                        email = emailOrg.value,
                        password = passwordOrg.value,
                        license = licenseOrg.value,
                        phone = phoneNum.value,
                        category = apiCategory,
                        address = addressOrg.value,
                        description = descriptionOrg.value
                    )

                    // 1. التحقق محلياً
                    val validationResult = AuthValidator.validateSignupErrors(request, confirmPasswordOrg.value)
                    errors = validationResult

                    if (!validationResult.hasError()) {
                        // 2. إرسال الطلب عبر الـ ViewModel
                        viewModel.registerOrganization(
                            org = request,
                            confPassword = confirmPasswordOrg.value
                        ) { success, message ->
                            if (success) {
                                Toast.makeText(context, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.LoginScreen.rout) {
                                    popUpTo(Screen.SignupScreen.rout) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, message ?: "Registration Failed", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please correct the errors in the form", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
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
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    val image = if (passwordVisible) R.drawable.view else R.drawable.close_eye
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