package com.example.gimnasio.ui

import com.example.gimnasio.R
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.gimnasio.data.model.Membresia
import com.example.gimnasio.viewmodel.MembresiasViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarMembresiaScreen(
    navController: NavController,
    viewModel: MembresiasViewModel = viewModel()
) {
    val context = LocalContext.current

    var tipo by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Agregar Membresía") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (tipo.isNotBlank() && costo.isNotBlank() && duracion.isNotBlank()) {
                    viewModel.insertarMembresia(
                        Membresia(
                            tipo = tipo,
                            costo = costo.toDoubleOrNull() ?: 0.0,
                            duracionDias = duracion.toIntOrNull() ?: 0
                        )
                    )
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
                value = tipo,
                onValueChange = { tipo = it },
                label = { Text("Tipo de Membresía") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = costo,
                onValueChange = { costo = it },
                label = { Text("Costo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = duracion,
                onValueChange = { duracion = it },
                label = { Text("Duración en días") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}