package com.example.gimnasio.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.gimnasio.MainScreenActivity
import com.example.gimnasio.R
import com.example.gimnasio.data.AppDatabase
import com.example.gimnasio.data.db.FirestoreSyncService
import com.example.gimnasio.ui.theme.*
import com.example.gimnasio.worker.AutoBackupWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class LoginActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val RC_SIGN_IN = 100
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        // Verificar sesión existente
        firebaseAuth.currentUser?.let { user ->
            handleUserLoggedIn(user, sharedPreferences)
            return
        }

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            val viewModel: LoginViewModel = viewModel()
            LoginScreen(
                onGoogleSignInClick = { startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN) },
                onLoginSuccess = { email ->
                    sharedPreferences.edit().putString("USER_EMAIL", email).apply()
                },
                viewModel = viewModel
            )
        }
    }

    private fun handleUserLoggedIn(user: FirebaseUser, sharedPreferences: SharedPreferences) {
        sharedPreferences.edit().putString("USER_EMAIL", user.email).apply()

        scope.launch {
            try {
                // Mostrar progreso
                Toast.makeText(
                    this@LoginActivity,
                    "Verificando tus datos...",
                    Toast.LENGTH_SHORT
                ).show()

                // Verificar y restaurar datos
                val db = AppDatabase.getDatabase(applicationContext)
                val syncService = FirestoreSyncService(
                    db.usuarioDao(),
                    db.membresiaDao(),
                    db.inscripcionDao()
                )

                // Verificar datos en Firestore
                val hasData = withContext(Dispatchers.IO) {
                    syncService.checkUserDataExists(user.uid)
                }

                if (hasData) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Restaurando tus datos...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // Restaurar datos
                    withContext(Dispatchers.IO) {
                        syncService.restoreAllWithResult()
                    }
                }

                // Programar worker y redirigir
                AutoBackupWorker.schedulePeriodicWork(this@LoginActivity)
                startActivity(Intent(this@LoginActivity, MainScreenActivity::class.java))
                finish()

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error al cargar datos: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                    // Redirigir a pesar del error
                    startActivity(Intent(this@LoginActivity, MainScreenActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data!!).getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            task.result?.user?.let { user ->
                                handleUserLoggedIn(user, getSharedPreferences("UserSession", Context.MODE_PRIVATE))
                            }
                        } else {
                            Toast.makeText(this, "Error al autenticar con Google", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}

@Composable
fun LoginScreen(
    onGoogleSignInClick: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
    ) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = GymDarkBlue
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = GymDarkBlue
                )
            )

            Spacer(Modifier.height(32.dp))

            // Campo de email/teléfono
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Correo electrónico o número", color = GymDarkBlue) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GymLightGray.copy(alpha = 0.2f),
                    unfocusedContainerColor = GymLightGray.copy(alpha = 0.2f),
                    focusedIndicatorColor = GymMediumBlue,
                    unfocusedIndicatorColor = GymMediumGray,
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña", color = GymDarkBlue) },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GymLightGray.copy(alpha = 0.2f),
                    unfocusedContainerColor = GymLightGray.copy(alpha = 0.2f),
                    focusedIndicatorColor = GymMediumBlue,
                    unfocusedIndicatorColor = GymMediumGray,
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Botón de inicio de sesión
            Button(
                onClick = {
                    viewModel.login(
                        context = context,
                        onSuccess = onLoginSuccess,
                        onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GymDarkBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        "Iniciar Sesión",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Botón de registro
            Button(
                onClick = {
                    viewModel.register(
                        context = context,
                        onSuccess = onLoginSuccess,
                        onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GymWhite,
                    contentColor = GymDarkBlue
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, GymDarkBlue)
            ) {
                Text(
                    "Registrar",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            // Divisor
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(
                    color = GymLightBlue,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "O",
                    color = GymMediumBlue,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Divider(
                    color = GymLightBlue,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Botón de Google
            OutlinedButton(
                onClick = onGoogleSignInClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = GymDarkBlue
                ),
                border = BorderStroke(1.dp, GymLightBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Iniciar con Google",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private var _email by mutableStateOf("")
    private var _password by mutableStateOf("")
    private var _isLoading by mutableStateOf(false)

    val email: String get() = _email
    val password: String get() = _password
    val isLoading: Boolean get() = _isLoading

    fun onEmailChange(value: String) { _email = value.trim() }
    fun onPasswordChange(value: String) { _password = value.trim() }

    fun login(context: Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        if (_isLoading) return

        _isLoading = true
        val input = _email
        val password = _password

        when {
            input.isBlank() -> {
                onError("Ingrese correo o teléfono")
                _isLoading = false
            }
            android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches() -> {
                handleEmailLogin(input, password, onSuccess, onError)
            }
            android.util.Patterns.PHONE.matcher(input).matches() -> {
                handlePhoneLogin(input, context as ComponentActivity, onSuccess, onError)
            }
            else -> {
                onError("Formato no válido")
                _isLoading = false
            }
        }
    }

    private fun handleEmailLogin(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading = false
                if (task.isSuccessful) {
                    onSuccess(email)
                } else {
                    onError(task.exception?.message ?: "Error al iniciar sesión")
                }
            }
    }

    private fun handlePhoneLogin(phone: String, activity: ComponentActivity, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            _isLoading = false
                            if (task.isSuccessful) {
                                onSuccess(phone)
                            } else {
                                onError("Error al verificar número")
                            }
                        }
                }

                override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                    _isLoading = false
                    onError("Error de verificación: ${e.message}")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // Implementar lógica para manejar el código SMS
                    onError("Se envió código SMS. Implementa la verificación.")
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun register(context: Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        if (_isLoading) return

        _isLoading = true
        val email = _email
        val password = _password

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onError("Solo registro con correo válido")
            _isLoading = false
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading = false
                if (task.isSuccessful) {
                    onSuccess(email)
                } else {
                    onError(task.exception?.message ?: "Error al registrar")
                }
            }
    }
}