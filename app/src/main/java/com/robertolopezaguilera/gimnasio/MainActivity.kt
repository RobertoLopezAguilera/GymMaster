package com.robertolopezaguilera.gimnasio

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.google.firebase.auth.FirebaseAuth
import com.robertolopezaguilera.gimnasio.ui.LoginActivity
import com.robertolopezaguilera.gimnasio.ui.VideoSplashScreen
import com.robertolopezaguilera.gimnasio.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                var splashShown by rememberSaveable { mutableStateOf(false) }
                val firebaseAuth = FirebaseAuth.getInstance()

                if (!splashShown) {
                    VideoSplashScreen(
                        onFinish = {
                            splashShown = true
                        }
                    )
                } else {
                    // Verificar autenticación y redirigir a la actividad correspondiente
                    LaunchedEffect(Unit) {
                        val currentUser = firebaseAuth.currentUser

                        if (currentUser != null) {
                            // Usuario ya autenticado, ir directamente a MainScreenActivity
                            startActivity(Intent(this@MainActivity, MainScreenActivity::class.java))
                        } else {
                            // Usuario no autenticado, ir a LoginActivity
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        }
                        finish() // Finaliza MainActivity para que no se pueda regresar
                    }
                }
            }
        }
    }
}