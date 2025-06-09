package com.example.gimnasio.ui

import com.example.gimnasio.R
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarUsuarioScreen(
    navController: NavController,
    viewModel: UsuarioViewModel = viewModel()
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var experiencia by remember { mutableStateOf("") }

    // Opciones para Dropdown
    val generos = listOf("Masculino", "Femenino", "Otro")
    val niveles = listOf("Principiante", "Intermedio", "Avanzado", "Mixto")

    var generoExpanded by remember { mutableStateOf(false) }
    var experienciaExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Agregar Usuario") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (nombre.isNotBlank() && genero.isNotBlank()) {
                    val usuario = Usuario(
                        nombre = nombre,
                        genero = genero,
                        edad = edad.toIntOrNull() ?: 0,
                        peso = peso.toDoubleOrNull() ?: 0.0,
                        experiencia = experiencia
                    )
                    viewModel.insertarUsuario(usuario)
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(painter = painterResource(id = R.drawable.ic_save), contentDescription = "Guardar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Selección de género
            ExposedDropdownMenuBox(
                expanded = generoExpanded,
                onExpandedChange = { generoExpanded = !generoExpanded }
            ) {
                OutlinedTextField(
                    value = genero,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Género") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = generoExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = generoExpanded,
                    onDismissRequest = { generoExpanded = false }
                ) {
                    generos.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                genero = opcion
                                generoExpanded = false
                            }
                        )
                    }
                }
            }

            // Edad y Peso en la misma fila
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it },
                    label = { Text("Edad") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                    label = { Text("Experiencia") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = experienciaExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = experienciaExpanded,
                    onDismissRequest = { experienciaExpanded = false }
                ) {
                    niveles.forEach { nivel ->
                        DropdownMenuItem(
                            text = { Text(nivel) },
                            onClick = {
                                experiencia = nivel
                                experienciaExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}