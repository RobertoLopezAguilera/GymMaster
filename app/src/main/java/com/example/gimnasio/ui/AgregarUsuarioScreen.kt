package com.example.gimnasio.ui

import com.example.gimnasio.R
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.example.gimnasio.data.model.Usuario
import com.example.gimnasio.viewmodel.UsuarioViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import com.example.gimnasio.ui.theme.*
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarUsuarioScreen(
    navController: NavController,
    viewModel: UsuarioViewModel = viewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var nombre by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var experiencia by remember { mutableStateOf("") }

    val generos = listOf("Masculino", "Femenino", "Otro")
    val niveles = listOf("Principiante", "Intermedio", "Avanzado", "Mixto")

    var generoExpanded by remember { mutableStateOf(false) }
    var experienciaExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = {
                    Text(
                        "Nombre completo *",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "Nombre",
                        tint = GymMediumBlue
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GymWhite,
                    unfocusedContainerColor = GymWhite,
                    focusedIndicatorColor = GymBrightRed,
                    unfocusedIndicatorColor = GymMediumBlue,
                    focusedLabelColor = GymDarkBlue,
                    unfocusedLabelColor = GymMediumBlue
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Selección de género
            ExposedDropdownMenuBox(
                expanded = generoExpanded,
                onExpandedChange = { generoExpanded = !generoExpanded },
            ) {
                OutlinedTextField(
                    value = genero,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            "Género *",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gender),
                            contentDescription = "Género",
                            tint = GymMediumBlue
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = generoExpanded
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GymWhite,
                        unfocusedContainerColor = GymWhite,
                        focusedIndicatorColor = GymBrightRed,
                        unfocusedIndicatorColor = GymMediumBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = generoExpanded,
                    onDismissRequest = { generoExpanded = false },
                    modifier = Modifier.background(GymLightGray)
                ) {
                    generos.forEach { opcion ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    opcion,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                genero = opcion
                                generoExpanded = false
                            }
                        )
                    }
                }
            }

            // Edad y Peso en la misma fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = edad,
                    onValueChange = { if (it.all { char -> char.isDigit() }) edad = it },
                    label = { Text("Edad") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_duration),
                            contentDescription = "Edad",
                            tint = GymMediumBlue
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GymWhite,
                        unfocusedContainerColor = GymWhite,
                        focusedIndicatorColor = GymBrightRed,
                        unfocusedIndicatorColor = GymMediumBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Peso (kg)") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_weight),
                            contentDescription = "Peso",
                            tint = GymMediumBlue
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GymWhite,
                        unfocusedContainerColor = GymWhite,
                        focusedIndicatorColor = GymBrightRed,
                        unfocusedIndicatorColor = GymMediumBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            // Selección de experiencia
            ExposedDropdownMenuBox(
                expanded = experienciaExpanded,
                onExpandedChange = { experienciaExpanded = !experienciaExpanded }
            ) {
                OutlinedTextField(
                    value = experiencia,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nivel de experiencia") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_membresia),
                            contentDescription = "Experiencia",
                            tint = GymMediumBlue
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = experienciaExpanded
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GymWhite,
                        unfocusedContainerColor = GymWhite,
                        focusedIndicatorColor = GymBrightRed,
                        unfocusedIndicatorColor = GymMediumBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = experienciaExpanded,
                    onDismissRequest = { experienciaExpanded = false },
                    modifier = Modifier.background(GymLightGray)
                ) {
                    niveles.forEach { nivel ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    nivel,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                experiencia = nivel
                                experienciaExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Botón de guardar en lugar del FAB
        Button(
            onClick = {
                if (nombre.isNotBlank() && genero.isNotBlank()) {
                    val hoy = LocalDate.now()
                    val usuario = Usuario(
                        nombre = nombre,
                        genero = genero,
                        edad = edad.toIntOrNull() ?: 0,
                        peso = peso.toDoubleOrNull() ?: 0.0,
                        experiencia = experiencia,
                        fechaInscripcion = hoy.toString(),
                        lastUpdated = System.currentTimeMillis()
                    )
                    viewModel.insertarUsuario(usuario)
                    navController.popBackStack()
                } else {
                    Toast.makeText(
                        context,
                        "Completa los campos obligatorios",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                focusManager.clearFocus()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GymBrightRed,
                contentColor = GymWhite
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = "Guardar Usuario",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
