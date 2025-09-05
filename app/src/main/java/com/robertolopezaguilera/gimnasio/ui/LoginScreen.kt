package com.robertolopezaguilera.gimnasio.ui

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
import androidx.compose.runtime.LaunchedEffect
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
import com.robertolopezaguilera.gimnasio.MainScreenActivity
import com.robertolopezaguilera.gimnasio.data.AppDatabase
import com.robertolopezaguilera.gimnasio.data.db.FirestoreSyncService
import com.robertolopezaguilera.gimnasio.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.robertolopezaguilera.gimnasio.worker.FirestoreSyncWorker
import com.robertolopezaguilera.gimnasio.R
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes

class LoginActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val RC_SIGN_IN = 100
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    // Variable para controlar si ya se verificó la autenticación
    private var authChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        // Verificar sesión existente inmediatamente
        firebaseAuth.currentUser?.let { user ->
            handleUserLoggedIn(user, sharedPreferences)
            authChecked = true
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
                onGoogleSignInClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                },
                onLoginSuccess = { email ->
                    sharedPreferences.edit().putString("USER_EMAIL", email).apply()
                },
                viewModel = viewModel
            )
        }
    }

    override fun onStart() {
        super.onStart()
        // Solo verificar autenticación si no se ha hecho ya
        if (!authChecked) {
            firebaseAuth.currentUser?.let { user ->
                val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                handleUserLoggedIn(user, sharedPreferences)
                authChecked = true
            }
        }
    }

    private fun handleUserLoggedIn(user: FirebaseUser, sharedPreferences: SharedPreferences) {
        // Marcar que ya verificamos la autenticación
        authChecked = true

        sharedPreferences.edit().putString("USER_EMAIL", user.email).apply()

        val restoredKey = "USER_DATA_RESTORED_${user.uid}"
        val wasRestored = sharedPreferences.getBoolean(restoredKey, false)

        scope.launch {
            try {
                if (!wasRestored) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Verificando tus datos por primera vez...",
                        Toast.LENGTH_SHORT
                    ).show()

                    val db = AppDatabase.getDatabase(applicationContext)
                    val syncService = FirestoreSyncService(
                        db.usuarioDao(),
                        db.membresiaDao(),
                        db.inscripcionDao(),
                        applicationContext
                    )

                    val hasData = withContext(Dispatchers.IO) {
                        syncService.checkUserDataExists(user.uid)
                    }

                    if (hasData) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Restaurando tus datos...",
                            Toast.LENGTH_SHORT
                        ).show()

                        withContext(Dispatchers.IO) {
                            syncService.restoreAllWithResult()
                        }
                    }

                    sharedPreferences.edit().putBoolean(restoredKey, true).apply()
                }

                scheduleSyncWorker(applicationContext)
                startActivity(Intent(this@LoginActivity, MainScreenActivity::class.java))
                finish()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@LoginActivity,
                    "Error al cargar datos: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this@LoginActivity, MainScreenActivity::class.java))
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { authTask ->
                        if (authTask.isSuccessful) {
                            // Usuario autenticado correctamente con Google
                            val user = authTask.result?.user
                            if (user != null) {
                                val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                                handleUserLoggedIn(user, sharedPreferences)
                            }
                        } else {
                            val exception = authTask.exception
                            val errorMessage = when (exception) {
                                is FirebaseAuthInvalidCredentialsException -> "Credenciales de Google inválidas"
                                is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con este email"
                                else -> "Error al autenticar con Google: ${exception?.message}"
                            }
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                val errorMessage = when (e.statusCode) {
                    GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Inicio de sesión cancelado"
                    GoogleSignInStatusCodes.SIGN_IN_FAILED -> "Error en inicio de sesión"
                    GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> "Inicio de sesión en progreso"
                    else -> "Error de Google Sign-In: ${e.statusCode}"
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}

private fun scheduleSyncWorker(context: Context) {
    val syncRequest = PeriodicWorkRequestBuilder<FirestoreSyncWorker>(
        4, TimeUnit.HOURS
    )
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "SyncWorker",
        ExistingPeriodicWorkPolicy.UPDATE,
        syncRequest
    )
}

@Composable
fun LoginScreen(
    onGoogleSignInClick: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current
    var showTermsDialog by remember { mutableStateOf(false) }

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
        if (showTermsDialog) {
            TermsAndConditionsDialog(
                onAccept = {
                    showTermsDialog = false
                    val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("TERMS_ACCEPTED", true).apply()
                },
                onDecline = {
                    showTermsDialog = false
                    Toast.makeText(context, "Debes aceptar los términos para continuar", Toast.LENGTH_SHORT).show()
                }
            )
        }
        LaunchedEffect(Unit) {
            val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            val termsAccepted = prefs.getBoolean("TERMS_ACCEPTED", false)

            if (!termsAccepted) {
                showTermsDialog = true
            }
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
                label = { Text("Correo electrónico", color = GymDarkBlue) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                visualTransformation = if (viewModel.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        Icon(
                            painter = painterResource(
                                id = if (viewModel.isPasswordVisible) {
                                    R.drawable.ic_visibility
                                } else {
                                    R.drawable.ic_visibility_off
                                }
                            ),
                            contentDescription = if (viewModel.isPasswordVisible) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            },
                            tint = GymDarkBlue
                        )
                    }
                },
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

    var isPasswordVisible by mutableStateOf(false)
        private set

    fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
    }

    fun login(context: Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        if (_isLoading) return

        _isLoading = true
        val input = _email
        val password = _password

        when {
            input.isBlank() -> {
                onError("Ingrese correo electrónico")
                _isLoading = false
                return
            }
            android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches() -> {
                handleEmailLogin(input, password, onSuccess, onError)
            }
            else -> {
                onError("Formato de correo no válido")
                _isLoading = false
            }
        }
    }

    private fun handleEmailLogin(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading = false
                if (task.isSuccessful) {
                    // REMOVIDA LA VERIFICACIÓN DE EMAIL - Permitir login sin verificación
                    val user = auth.currentUser
                    if (user != null) {
                        onSuccess(email)
                    } else {
                        onError("Error al obtener información del usuario")
                    }
                } else {
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is FirebaseAuthInvalidUserException -> "Usuario no encontrado"
                        is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas"
                        is FirebaseAuthUserCollisionException -> "Usuario ya existe con diferentes credenciales"
                        else -> exception?.message ?: "Error al iniciar sesión"
                    }
                    onError(errorMessage)
                }
            }
    }

    fun register(context: Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        if (_isLoading) return

        _isLoading = true
        val email = _email
        val password = _password

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onError("Ingrese un correo válido")
            _isLoading = false
            return
        }

        if (password.length < 6) {
            onError("La contraseña debe tener al menos 6 caracteres")
            _isLoading = false
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading = false
                if (task.isSuccessful) {
                    // REMOVIDO EL ENVÍO DE EMAIL DE VERIFICACIÓN
                    val user = auth.currentUser
                    if (user != null) {
                        onSuccess(email)
                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    } else {
                        onError("Error al crear usuario")
                    }
                } else {
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is FirebaseAuthWeakPasswordException -> "Contraseña demasiado débil"
                        is FirebaseAuthInvalidCredentialsException -> "Email inválido"
                        is FirebaseAuthUserCollisionException -> "El usuario ya existe"
                        else -> exception?.message ?: "Error al registrar"
                    }
                    onError(errorMessage)
                }
            }
    }

    // Método para resetear contraseña
    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Error al enviar email de reset")
                }
            }
    }
}