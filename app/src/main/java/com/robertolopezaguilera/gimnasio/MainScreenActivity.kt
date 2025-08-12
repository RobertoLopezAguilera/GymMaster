package com.robertolopezaguilera.gimnasio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.robertolopezaguilera.gimnasio.ui.MainScreen
import com.robertolopezaguilera.gimnasio.ui.theme.MyApplicationTheme

class MainScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}
