package com.example.gimnasio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.gimnasio.ui.MainScreen
import com.example.gimnasio.ui.VideoSplashScreen
import com.example.gimnasio.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // scope Composable válido
                var splashShown by rememberSaveable { mutableStateOf(false) }

                if (!splashShown) {
                    VideoSplashScreen(
                        onFinish = { splashShown = true }
                    )
                } else {
                    MainScreen()
                }
            }
        }
    }
}
