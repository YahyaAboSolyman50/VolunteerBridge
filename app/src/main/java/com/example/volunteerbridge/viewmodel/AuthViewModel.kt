package com.example.volunteerbridge.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.volunteerbridge.model.*
import com.example.volunteerbridge.model.classes.*
import com.example.volunteerbridge.model.classes.status.AuthState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val auth = Firebase.auth
    private val firestore = FirebaseFirestore.getInstance()

    // --- States ---
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _errors = MutableStateFlow(SignupErrors())
    val errors = _errors.asStateFlow()

    private val _userType = MutableStateFlow<UserType>(UserType.Loading)
    val userType: StateFlow<UserType> = _userType.asStateFlow()

    // ❌ تم حذف كائنات التخزين القديمة المسببة للتكرار والمشاكل برمجياً (_currentUserData و _currentOrgData)

    private val sharedPrefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private fun saveUserSession(uid: String, type: String) {
        sharedPrefs.edit().apply {
            putString("user_id", uid)
            putString("user_type", type)
            apply()
        }
    }

    fun checkSavedSession() {
        val savedUid = sharedPrefs.getString("user_id", null)
        val savedType = sharedPrefs.getString("user_type", null)
        if (savedUid != null && savedType != null) {
            _userType.value = when (savedType) {
                "student" -> UserType.Student
                "organization" -> UserType.Organization
                else -> UserType.Loading
            }
            checkUserAndData()
        } else {
            checkUserAndData()
        }
    }

    fun registerOrganization(regOrg: Organization, confPassword: String) {
        val validationErrors = AuthValidator.validateSignupErrors(regOrg, confPassword)
        _errors.value = validationErrors
        if (validationErrors.hasError()) return
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(regOrg.emailOrg, regOrg.passwordOrg).await()
                val uid = result.user?.uid ?: ""
                val org = regOrg.copy(uid = uid, verified = false)
                firestore.collection("organizations")
                    .document(uid)
                    .set(org)
                    .await()
                result.user?.sendEmailVerification()
                saveUserSession(uid, "organization")
                _userType.value = UserType.Organization
                _authState.value = AuthState.Success(result.user)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(handleError(e))
            }
        }
    }

    fun loginUser(loginRequest: LoginRequest, rememberMe: Boolean) {
        val identifier = loginRequest.studentId.trim()
        val password = loginRequest.password

        if (identifier.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Please fill all fields")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                var finalEmail = identifier

                if (identifier.all { it.isDigit() }) {
                    var studentQuery = firestore.collection("student")
                        .whereEqualTo("studentId", identifier)
                        .get()
                        .await()

                    if (studentQuery.isEmpty) {
                        val studentIdNum = identifier.toLongOrNull()
                        if (studentIdNum != null) {
                            studentQuery = firestore.collection("student")
                                .whereEqualTo("studentId", studentIdNum)
                                .get()
                                .await()

                        }
                    }

                    if (!studentQuery.isEmpty) {
                        finalEmail = studentQuery.documents[0].getString("email") ?: ""
                    } else {
                        _authState.value = AuthState.Error("University ID not found")
                        return@launch
                    }
                }

                val result = auth.signInWithEmailAndPassword(finalEmail, password).await()
                val uid = result.user?.uid ?: ""

                // 🟢 السر هنا: ننتظر حتى ينتهي فحص السيرفر وتخزين الجلسة بالكامل أولاً!
                fetchUserTypeAndDataSuspended(uid, shouldSave = rememberMe)

                // الآن نعلن النجاح بعد استقرار الـ userType تماماً في الذاكرة
                _authState.value = AuthState.Success(result.user)

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login error: ${e.message}")
                _authState.value = AuthState.Error(handleError(e))
            }
        }
    }

    fun checkUserAndData() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _userType.value = UserType.Guest
            return
        }
        viewModelScope.launch {
            fetchUserTypeAndDataSuspended(uid, shouldSave = false)
        }
    }

    private suspend fun fetchUserTypeAndDataSuspended(uid: String, shouldSave: Boolean) {
        _userType.value = UserType.Loading
        try {
            val studentDoc = firestore.collection("student").document(uid).get().await()
            if (studentDoc.exists()) {
                if (shouldSave) saveUserSession(uid, "student")
                _userType.value = UserType.Student
            } else {
                val orgDoc = firestore.collection("organizations").document(uid).get().await()
                if (orgDoc.exists()) {
                    if (shouldSave) saveUserSession(uid, "organization")
                    _userType.value = UserType.Organization
                } else {
                    _userType.value = UserType.Error
                }
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error fetching user data: ${e.message}")
            _userType.value = UserType.Error
        }
    }

    private fun handleError(e: Exception): String {
        return when (e) {
            is FirebaseAuthUserCollisionException -> "Email already in use"
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
            else -> e.localizedMessage ?: "Something went wrong"
        }
    }

    fun resetAuthState() { _authState.value = AuthState.Idle }

    fun logout() {
        auth.signOut()
        sharedPrefs.edit().clear().apply()
        _userType.value = UserType.Guest
        _authState.value = AuthState.Idle
    }
}