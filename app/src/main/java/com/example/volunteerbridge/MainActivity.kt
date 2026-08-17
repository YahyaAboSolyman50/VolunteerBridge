package com.example.volunteerbridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.volunteerbridge.data.model.TokenManager
import com.example.volunteerbridge.nav.Nav
import com.example.volunteerbridge.network.RetrofitClient
import com.example.volunteerbridge.ui.theme.VolunteerBridgeTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(this)
        enableEdgeToEdge()
        setContent {
            VolunteerBridgeTheme {
                // 2. استخدام Surface لتعيين لون الخلفية الافتراضي للثيم
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Nav(this)
                }
            }
        }
    }
}


