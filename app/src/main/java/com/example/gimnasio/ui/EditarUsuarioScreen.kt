package com.example.gimnasio.ui

import android.widget.Toast
import com.example.gimnasio.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.example.gimnasio.data.model.Usuario
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.gimnasio.viewmodel.UsuarioDetalleViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarUsuarioScreen(
    usuarioId: Int,
    navController: NavController,
    viewModel: UsuarioDetalleViewModel = viewModel()
) {
    val context = LocalContext.current
    val usuario by viewModel.getUsuario(usuarioId).collectAsState(initial = null)
    var nombre by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var experiencia by remember { mutableStateOf("") }

    var generoExpanded by remember { mutableStateOf(false) }
    var expExpanded by remember { mutableStateOf(false) }

    // Inicializar valores cuando usuario cambia
    LaunchedEffect(usuario) {
        usuario?.let {
            nombre = it.nombre
            genero = it.genero
            edad = it.edad.toString()
            peso = it.peso.toString()
            experiencia = it.experiencia
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Editar Usuario") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (nombre.isNotBlank() && genero.isNotBlank() && experiencia.isNotBlank()) {
                    val hoy = LocalDate.now()
                    val updatedUsuario = Usuario(
                        id = usuarioId,
                        nombre = nombre,
                        genero = genero,
                        edad = edad.toIntOrNull() ?: 0,
                        peso = peso.toDoubleOrNull() ?: 0.0,
                        experiencia = experiencia,
                        fechaInscripcion = hoy.toString()
                    )
                    viewModel.actualizarUsuario(updatedUsuario)
                    Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(painter = painterResource(id = R.drawable.ic_save), contentDescription = "Guardar cambios")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true
            )

            // GÉNERO dropdown
            Box {
                OutlinedTextField(
                    value = genero,
                    onValueChange = {},
                    label = { Text("Género") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, null,
                            Modifier.clickable { generoExpanded = true })
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = generoExpanded,
                    onDismissRequest = { generoExpanded = false }
                ) {
                    listOf("Masculino", "Femenino", "Otro").forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                genero = it
                                generoExpanded = false
                            }
                        )
                    }
                }
            }

            // Edad y Peso en misma fila
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it },
                    label = { Text("Edad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            // EXPERIENCIA dropdown
            Box {
                OutlinedTextField(
                    value = experiencia,
                    onValueChange = {},
                    label = { Text("Experiencia") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, null,
                            Modifier.clickable { expExpanded = true })
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expExpanded,
                    onDismissRequest = { expExpanded = false }
                ) {
                    listOf("Principiante", "Intermedio", "Avanzado", "Mixto").forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                experiencia = it
                                expExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}