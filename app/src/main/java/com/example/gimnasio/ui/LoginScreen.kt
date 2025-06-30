package com.example.gimnasio.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.runtime.State
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import com.example.gimnasio.MainActivity
import com.example.gimnasio.MainScreenActivity
import com.example.gimnasio.R
import com.example.gimnasio.ui.theme.*
import java.util.concurrent.TimeUnit

class LoginActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null && !userEmail.isNullOrEmpty()) {
            startActivity(Intent(this, MainScreenActivity::class.java))
            finish()
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            LoginScreen(
                onGoogleSignInClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                },
                onLoginSuccess = { email ->
                    sharedPreferences.edit().putString("USER_EMAIL", email).apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val email = firebaseAuth.currentUser?.email
                                if (!email.isNullOrEmpty()) {
                                    val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                                    sharedPreferences.edit().putString("USER_EMAIL", email).apply()
                                    startActivity(Intent(this, MainScreenActivity::class.java))
                                    finish()
                                }
                            } else {
                                Toast.makeText(this, "Error con Firebase", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun LoginScreen(
    onGoogleSignInClick: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val email by viewModel.email
    val password by viewModel.password

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
    ) {
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
                value = email,
                onValueChange = viewModel::onEmailChange,
                label = {
                    Text(
                        "Correo electrónico o número",
                        color = GymDarkBlue
                    )
                },
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
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = {
                    Text(
                        "Contraseña",
                        color = GymDarkBlue
                    )
                },
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
                        onSuccess = { onLoginSuccess(it) },
                        onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GymDarkBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Iniciar Sesión",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Botón de registro
            Button(
                onClick = {
                    viewModel.register(
                        context = context,
                        onSuccess = { onLoginSuccess(it) },
                        onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
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

    private val _email = mutableStateOf("")
    val email: State<String> get() = _email

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }

    fun login(context: Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val correoONumero = email.value
        val contrasena = password.value

        if (correoONumero.isEmpty()) {
            onError("Campo vacío")
            return
        }

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(correoONumero).matches()) {
            auth.signInWithEmailAndPassword(correoONumero, contrasena)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) onSuccess(correoONumero)
                    else onError("Error al iniciar sesión con correo")
                }
        } else if (android.util.Patterns.PHONE.matcher(correoONumero).matches()) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(correoONumero)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(context as ComponentActivity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) onSuccess(correoONumero)
                                else onError("Error al iniciar sesión con número")
                            }
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        onError("Verificación fallida: ${e.localizedMessage}")
                    }

                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                        onError("Código enviado. Agrega lógica para verificar el código recibido.")
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } else {
            onError("Formato de entrada no válido")
        }
    }

    fun register(context: Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val correoONumero = email.value
        val contrasena = password.value

        if (correoONumero.isEmpty()) {
            onError("Campo vacío")
            return
        }

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(correoONumero).matches()) {
            auth.createUserWithEmailAndPassword(correoONumero, contrasena)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) onSuccess(correoONumero)
                    else onError("Error al registrar usuario")
                }
        } else {
            onError("Solo se permite registrar con correo, el inicio por número solo está disponible para login")
        }
    }
}
