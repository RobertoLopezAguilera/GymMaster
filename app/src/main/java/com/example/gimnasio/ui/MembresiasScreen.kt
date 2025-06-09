package com.example.gimnasio.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.example.gimnasio.data.model.Membresia
import com.example.gimnasio.viewmodel.MembresiasViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MembresiasScreen(
    viewModel: MembresiasViewModel = viewModel(),
    navController: NavHostController
) {
    val membresias by viewModel.membresias.collectAsState()

    // Estados para el menú contextual
    var showMenu by remember { mutableStateOf(false) }
    var selectedMembresia by remember { mutableStateOf<Membresia?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn {
            itemsIndexed(membresias) { index, membresia ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                selectedMembresia = membresia
                                showMenu = true
                            }
                        ),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Tipo: ${membresia.tipo}")
                        Text(text = "Costo: $${membresia.costo}")
                        Text(text = "Duración: ${membresia.duracionDias} días")
                    }
                }
            }
        }

        // Menú contextual (opciones de editar / eliminar)
        if (showMenu && selectedMembresia != null) {
            AlertDialog(
                onDismissRequest = { showMenu = false },
                text = { Text("¿Qué deseas hacer?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.eliminarMembresia(selectedMembresia!!.id)
                        showMenu = false
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        navController.navigate("editar_membresia/${selectedMembresia!!.id}")
                        showMenu = false
                    }) {
                        Text("Editar")
                    }
                }
            )
        }
    }
}