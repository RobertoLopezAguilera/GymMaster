package com.example.gimnasio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gimnasio.ui.theme.*
import com.example.gimnasio.viewmodel.MembresiasViewModel

@Composable
fun EditarMembresiaScreen(
    membresiaId: Int,
    navController: NavHostController,
    viewModel: MembresiasViewModel = viewModel()
) {
    val membresia by viewModel.obtenerMembresiaPorId(membresiaId).collectAsState(initial = null)
    var tipo by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(membresia) {
        membresia?.let {
            tipo = it.tipo
            costo = it.costo.toString()
            duracion = it.duracionDias.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GymMediumBlue)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = GymWhite
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Editar Membresía",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = GymWhite,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        membresia?.let {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                item {
                    // Campo Tipo
                    Text(
                        text = "Tipo de Membresía",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GymDarkGray,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = tipo,
                        onValueChange = { tipo = it },
                        label = { Text("Ejemplo: Premium, Básica, etc.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GymWhite,
                            unfocusedContainerColor = GymWhite,
                            focusedIndicatorColor = GymMediumBlue,
                            unfocusedIndicatorColor = GymMediumGray,
                        ),
                        singleLine = true
                    )

                    // Campo Costo
                    Text(
                        text = "Costo Mensual",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GymDarkGray,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = costo,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) costo = it
                        },
                        label = { Text("Ingrese el costo en MXN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GymWhite,
                            unfocusedContainerColor = GymWhite,
                            focusedIndicatorColor = GymMediumBlue,
                            unfocusedIndicatorColor = GymMediumGray,
                        ),
                        singleLine = true,
                        leadingIcon = {
                            Text(
                                text = "$",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = GymDarkGray
                                )
                            )
                        }
                    )

                    // Campo Duración
                    Text(
                        text = "Duración en Días",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GymDarkGray,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = duracion,
                        onValueChange = {
                            if (it.isEmpty() || it.matches(Regex("^\\d+$"))) duracion = it
                        },
                        label = { Text("Ejemplo: 30, 90, 365") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = GymWhite,
                            unfocusedContainerColor = GymWhite,
                            focusedIndicatorColor = GymMediumBlue,
                            unfocusedIndicatorColor = GymMediumGray,
                        ),
                        singleLine = true,
                        trailingIcon = {
                            Text(
                                text = "días",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = GymDarkGray
                                )
                            )
                        }
                    )
                }
            }

            // Botón Guardar
            Button(
                onClick = {
                    when {
                        tipo.isBlank() || costo.isBlank() || duracion.isBlank() -> {
                            showErrorDialog = true
                        }
                        costo.toDoubleOrNull() == null -> {
                            showErrorDialog = true
                            // Podrías mostrar un mensaje diferente para error de formato numérico
                        }
                        duracion.toIntOrNull() == null -> {
                            showErrorDialog = true
                            // Podrías mostrar un mensaje diferente para error de formato numérico
                        }
                        else -> {
                            viewModel.actualizarMembresia(
                                it.copy(
                                    tipo = tipo,
                                    costo = costo.toDouble(),
                                    duracionDias = duracion.toInt()
                                )
                            )
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GymMediumBlue,
                    contentColor = GymWhite
                )
            ) {
                Text(
                    text = "GUARDAR CAMBIOS",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = GymMediumBlue)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando información de la membresía...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = GymDarkGray
                        )
                    )
                }
            }
        }
    }

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Text(
                    "Campos incompletos o inválidos",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GymBrightRed,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text("Por favor completa todos los campos con valores válidos")
            },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = GymMediumBlue
                    )
                ) {
                    Text("ENTENDIDO")
                }
            },
            containerColor = GymWhite,
            shape = RoundedCornerShape(12.dp)
        )
    }
}