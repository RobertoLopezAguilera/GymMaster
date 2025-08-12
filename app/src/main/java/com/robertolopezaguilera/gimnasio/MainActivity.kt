package com.robertolopezaguilera.gimnasio

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.robertolopezaguilera.gimnasio.ui.LoginActivity
import com.robertolopezaguilera.gimnasio.ui.VideoSplashScreen
import com.robertolopezaguilera.gimnasio.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                var splashShown by rememberSaveable { mutableStateOf(false) }

                if (!splashShown) {
                    VideoSplashScreen(
                        onFinish = {
                            splashShown = true
                        }
                    )
                } else {
                    // Navegar a LoginActivity al finalizar el splash
                    LaunchedEffect(Unit) {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish() // Finaliza MainActivity para que no se pueda regresar
                    }
                }
            }
        }
    }
}
