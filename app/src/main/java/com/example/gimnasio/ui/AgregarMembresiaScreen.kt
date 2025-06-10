package com.example.gimnasio.ui

import com.example.gimnasio.R
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import com.example.gimnasio.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Agregar Membresía",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GymMediumBlue,
                    titleContentColor = GymWhite,
                    navigationIconContentColor = GymWhite
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    when {
                        tipo.isBlank() -> {
                            errorMessage = "El tipo de membresía es requerido"
                            showErrorDialog = true
                        }
                        costo.isBlank() || costo.toDoubleOrNull() == null -> {
                            errorMessage = "Ingrese un costo válido"
                            showErrorDialog = true
                        }
                        duracion.isBlank() || duracion.toIntOrNull() == null -> {
                            errorMessage = "Ingrese una duración válida en días"
                            showErrorDialog = true
                        }
                        else -> {
                            viewModel.insertarMembresia(
                                Membresia(
                                    tipo = tipo,
                                    costo = costo.toDouble(),
                                    duracionDias = duracion.toInt()
                                )
                            )
                            navController.popBackStack()
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = "Guardar"
                    )
                },
                text = { Text("Guardar Membresía") },
                containerColor = GymMediumBlue,
                contentColor = GymWhite
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(GymLightGray)
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Campo Tipo
            Text(
                text = "Tipo de Membresía",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = GymDarkGray,
                    fontWeight = FontWeight.Medium
                )
            )
            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                label = { Text("Ejemplo: Premium, Básica, etc.") },
                modifier = Modifier.fillMaxWidth(),
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
                )
            )
            OutlinedTextField(
                value = costo,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) costo = it
                },
                label = { Text("Ingrese el costo en MXN") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
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
                )
            )
            OutlinedTextField(
                value = duracion,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d+$"))) duracion = it
                },
                label = { Text("Ejemplo: 30, 90, 365") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
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

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Text(
                    "Error en los datos",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GymBrightRed,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(errorMessage)
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