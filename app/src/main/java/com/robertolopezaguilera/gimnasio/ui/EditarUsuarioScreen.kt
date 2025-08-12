package com.robertolopezaguilera.gimnasio.ui

import android.app.DatePickerDialog
import com.robertolopezaguilera.gimnasio.R
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.robertolopezaguilera.gimnasio.data.model.Usuario
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import com.robertolopezaguilera.gimnasio.ui.theme.*
import com.robertolopezaguilera.gimnasio.viewmodel.UsuarioDetalleViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarUsuarioScreen(
    usuarioId: String,
    navController: NavController,
    viewModel: UsuarioDetalleViewModel = viewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val usuario by viewModel.getUsuario(usuarioId.toString()).collectAsState(initial = null)

    // Estados para los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var experiencia by remember { mutableStateOf("") }
    var fechaInscripcion by remember { mutableStateOf("") }

    // Opciones para los dropdowns
    val generos = listOf("Masculino", "Femenino", "Otro")
    val niveles = listOf("Principiante", "Intermedio", "Avanzado", "Mixto")

    var generoExpanded by remember { mutableStateOf(false) }
    var experienciaExpanded by remember { mutableStateOf(false) }

    // Inicializar valores cuando se carga el usuario
    LaunchedEffect(usuario) {
        usuario?.let {
            nombre = it.nombre ?: ""
            genero = it.genero ?: ""
            edad = it.edad?.toString() ?: ""
            peso = it.peso?.toString() ?: ""
            experiencia = it.experiencia ?: ""
            fechaInscripcion = it.fechaInscripcion ?: ""
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (nombre.isNotBlank() && genero.isNotBlank()) {
                        val updatedUsuario = Usuario(
                            id = usuarioId,
                            nombre = nombre,
                            genero = genero,
                            edad = edad.toIntOrNull() ?: 0,
                            peso = peso.toDoubleOrNull() ?: 0.0,
                            experiencia = experiencia,
                            fechaInscripcion = fechaInscripcion,
                            lastUpdated = System.currentTimeMillis()
                        )
                        viewModel.actualizarUsuario(updatedUsuario)
                        Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
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
                containerColor = GymBrightRed,
                contentColor = GymWhite
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_save),
                    contentDescription = "Guardar cambios"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(GymLightGray)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            Text(
                text = "Editar Usuario",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = GymDarkBlue,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Campo Nombre (obligatorio)
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

            // Selector de fecha de inscripción
            val calendar = Calendar.getInstance()
            val datePickerDialog = remember {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        fechaInscripcion = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fechaInscripcion,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de inscripción") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendario),
                            contentDescription = "Fecha de inscripción",
                            tint = GymMediumBlue
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendario),
                            contentDescription = "Abrir calendario",
                            tint = GymMediumBlue
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GymWhite,
                        unfocusedContainerColor = GymWhite,
                        focusedIndicatorColor = GymBrightRed,
                        unfocusedIndicatorColor = GymMediumBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Este Box transparente capturará todos los clicks
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { datePickerDialog.show() }
                        .background(color = Color.Transparent)
                )
            }

            Spacer(modifier = Modifier.height(80.dp)) // Espacio para el FAB
        }
    }
}