package com.example.volunteerbridge.view.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.volunteerbridge.app.R
import com.example.volunteerbridge.model.classes.status.AuthState
import com.example.volunteerbridge.model.classes.LoginRequest
import com.example.volunteerbridge.screens.Screen
import com.example.volunteerbridge.viewmodel.AdminViewModel
import com.example.volunteerbridge.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    val loginState by authViewModel.authState.collectAsState()

    var rememberMe by remember { mutableStateOf(false) }

    // استخراج ألوان الثيم الحالية
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(loginState) {
        if (loginState is AuthState.Success) {
            authViewModel.resetAuthState()
            navController.navigate(Screen.HomeScreen.rout) {
                popUpTo(Screen.LoginScreen.rout) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background) // يستخدم DarkBlueBg أو WhiteBg تلقائياً
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // العنوان الترحيبي
            Text(
                text = "Welcome Back",
                color = colorScheme.onBackground, // أسود في الفاتح، أبيض في الداكن
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Sign in to continue your journey",
                color = colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(60.dp))

            // حقل البريد الإلكتروني
            LoginInputField(
                label = "Email Address",
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = "example@mail.com"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // حقل كلمة المرور
            LoginInputField(
                label = "Password",
                value = password.value,
                onValueChange = { password.value = it },
                placeholder = "••••••••",
                isPassword = true
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colorScheme.primary,
                        uncheckedColor = colorScheme.onSurface.copy(alpha = 0.4f),
                        checkmarkColor = colorScheme.onPrimary
                    )
                )
                Text(
                    text = "Remember me",
                    color = colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(48.dp))

            // زر الدخول
            Button(
                onClick = {

                    authViewModel.loginUser(
                        loginRequest = LoginRequest(email.value, password.value),
                        rememberMe = rememberMe
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary // اللون فوق الزر
                ),
                enabled = loginState !is AuthState.Loading
            ) {
                if (loginState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "LOGIN",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SignUpFooter(navController)
        }

        if (loginState is AuthState.Error) {
            LaunchedEffect(loginState) {
                Toast.makeText(context, (loginState as AuthState.Error).message, Toast.LENGTH_LONG)
                    .show()
                authViewModel.resetAuthState()
            }
        }
    }
}

@Composable
fun LoginInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
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
            singleLine = true,
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
                focusedContainerColor = colorScheme.surface, // CardBgDark أو CardBgLight
                unfocusedContainerColor = colorScheme.surface,
                cursorColor = colorScheme.primary
            )
        )
    }
}

@Composable
private fun SignUpFooter(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Don't have an account?",
            fontSize = 14.sp,
            color = colorScheme.onBackground.copy(alpha = 0.6f)
        )
        TextButton(onClick = { navController.navigate(Screen.SignupScreen.rout) }) {
            Text(
                text = "Sign Up",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = colorScheme.primary
            )
        }
    }
}