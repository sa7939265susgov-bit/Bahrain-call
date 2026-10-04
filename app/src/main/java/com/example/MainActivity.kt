package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InCallScreen
import com.example.ui.theme.BahrainMeetTheme
import com.example.viewmodel.BahrainMeetViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BahrainMeetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BahrainMeetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BahrainMeetApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun BahrainMeetApp(viewModel: BahrainMeetViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()

    val isVerified = userProfile?.isVerified == true

    when {
        activeCall != null -> {
            InCallScreen(
                call = activeCall!!,
                viewModel = viewModel,
                onEndCall = { viewModel.endCall() }
            )
        }
        !isVerified -> {
            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = { /* profile auto updated */ }
            )
        }
        else -> {
            HomeScreen(viewModel = viewModel)
        }
    }
}

