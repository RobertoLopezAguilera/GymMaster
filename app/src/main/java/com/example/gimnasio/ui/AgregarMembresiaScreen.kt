package com.example.gimnasio.ui

import com.example.gimnasio.R
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gimnasio.data.model.Membresia
import com.example.gimnasio.viewmodel.MembresiasViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.example.gimnasio.ui.theme.*

@Composable
fun AgregarMembresiaScreen(
    navController: NavController,
    viewModel: MembresiasViewModel = viewModel()
) {
    var tipo by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
            .padding(horizontal = 24.dp)
    ) {
        // Formulario
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card para el formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GymWhite),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Campo Tipo
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Tipo de Membresía",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = GymDarkBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        OutlinedTextField(
                            value = tipo,
                            onValueChange = { tipo = it },
                            placeholder = { Text("Ejemplo: Premium, Básica") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = GymLightGray.copy(alpha = 0.2f),
                                unfocusedContainerColor = GymLightGray.copy(alpha = 0.2f),
                                focusedIndicatorColor = GymMediumBlue,
                                unfocusedIndicatorColor = GymMediumGray,
                            ),
                            singleLine = true
                        )
                    }

                    // Campo Costo
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Costo",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = GymDarkBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        OutlinedTextField(
                            value = costo,
                            onValueChange = {
                                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) costo = it
                            },
                            placeholder = { Text("0.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = GymLightGray.copy(alpha = 0.2f),
                                unfocusedContainerColor = GymLightGray.copy(alpha = 0.2f),
                                focusedIndicatorColor = GymMediumBlue,
                                unfocusedIndicatorColor = GymMediumGray,
                            ),
                            singleLine = true,
                            leadingIcon = {
                                Text(
                                    text = "$",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = GymDarkBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            },
                            trailingIcon = {
                                Text(
                                    text = "MXN",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = GymDarkGray
                                    )
                                )
                            }
                        )
                    }

                    // Campo Duración
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Duración",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = GymDarkBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        OutlinedTextField(
                            value = duracion,
                            onValueChange = {
                                if (it.isEmpty() || it.matches(Regex("^\\d+$"))) duracion = it
                            },
                            placeholder = { Text("30") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = GymLightGray.copy(alpha = 0.2f),
                                unfocusedContainerColor = GymLightGray.copy(alpha = 0.2f),
                                focusedIndicatorColor = GymMediumBlue,
                                unfocusedIndicatorColor = GymMediumGray,
                            ),
                            singleLine = true,
                            trailingIcon = {
                                Text(
                                    text = "días",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = GymDarkGray
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }

        // Botón de guardar
        Button(
            onClick = {
                when {
                    tipo.isBlank() -> {
                        errorMessage = "Debes especificar un tipo de membresía"
                        showErrorDialog = true
                    }
                    costo.isBlank() || costo.toDoubleOrNull() == null -> {
                        errorMessage = "Ingresa un costo válido"
                        showErrorDialog = true
                    }
                    duracion.isBlank() || duracion.toIntOrNull() == null -> {
                        errorMessage = "Ingresa una duración válida en días"
                        showErrorDialog = true
                    }
                    else -> {
                        viewModel.insertarMembresia(
                            Membresia(
                                tipo = tipo,
                                costo = costo.toDouble(),
                                duracionDias = duracion.toInt(),
                                lastUpdated = System.currentTimeMillis()
                            )
                        )
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, top = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GymMediumBlue,
                contentColor = GymWhite
            )
        ) {
            Text(
                text = "Guardar Membresía",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Text(
                    "Datos incompletos",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GymBrightRed,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    errorMessage,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = GymMediumBlue
                    )
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = GymWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}