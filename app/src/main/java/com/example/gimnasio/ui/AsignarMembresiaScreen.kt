package com.example.gimnasio.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.viewmodel.MembresiasViewModel
import com.example.gimnasio.viewmodel.UsuarioDetalleViewModel
import java.time.LocalDate

@Composable
fun AsignarMembresiaScreen(
    usuarioId: Int,
    navController: NavController,
    viewModel: MembresiasViewModel = viewModel(),
    usuarioDetalleViewModel: UsuarioDetalleViewModel = viewModel()
) {
    val membresias by viewModel.membresias.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "Selecciona una membresía para asignar",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

        LazyColumn {
            itemsIndexed(membresias) { _, membresia ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable{
                            val hoy = LocalDate.now()
                            val vencimiento = hoy.plusDays(membresia.duracionDias.toLong())

                            val inscripcion = Inscripcion(
                                idUsuario = usuarioId,
                                idMembresia = membresia.id,
                                fechaPago = hoy.toString(),
                                fechaVencimiento = vencimiento.toString(),
                                pagado = true
                            )

                            usuarioDetalleViewModel.insertarInscripcion(inscripcion)
                            navController.popBackStack() // volver a pantalla anterior
                        },
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
    }
}