package com.example.internshipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.internshipapp.presentation.NavGraphs
import com.example.internshipapp.ui.theme.InternshipAppTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        var keepSplash = true
        super.onCreate(savedInstanceState)
        splash.setKeepOnScreenCondition { keepSplash }
        lifecycleScope.launch {
            delay(2000)
            keepSplash = false
        }
        enableEdgeToEdge()
        setContent {
            InternshipAppTheme {
                DestinationsNavHost(
                    navGraph = NavGraphs.root
                )
            }
        }
        requestPermissions()
    }
    private fun requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.SEND_SMS),
                0
        )
    }
}




