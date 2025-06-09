package com.example.gimnasio.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gimnasio.viewmodel.MembresiasViewModel

@Composable
fun EditarMembresiaScreen(
    membresiaId: Int,
    navController: NavHostController,
    viewModel: MembresiasViewModel = viewModel()
){
    val membresia by viewModel.obtenerMembresiaPorId(membresiaId).collectAsState(initial = null)

    var tipo by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    LaunchedEffect(membresia) {
        membresia?.let {
            tipo = it.tipo
            costo = it.costo.toString()
            duracion = it.duracionDias.toString()
        }
    }

    membresia?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                label = { Text("Tipo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = costo,
                onValueChange = { costo = it },
                label = { Text("Costo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = duracion,
                onValueChange = { duracion = it },
                label = { Text("Duración (días)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.actualizarMembresia(
                        it.copy(
                            tipo = tipo,
                            costo = costo.toDoubleOrNull() ?: 0.0,
                            duracionDias = duracion.toIntOrNull() ?: 0
                        )
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Guardar")
            }
        }
    } ?: run {
        // Puedes mostrar un mensaje de carga o error si la membresía no se encuentra
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando membresía...")
        }
    }
}