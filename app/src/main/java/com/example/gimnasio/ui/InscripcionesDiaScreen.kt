package com.example.gimnasio.ui

import com.example.gimnasio.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gimnasio.viewmodel.UsuarioViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.example.gimnasio.viewmodel.InscripcionViewModel
import java.time.LocalDate

@Composable
fun InscripcionesDiaScreen(
    fecha: LocalDate,
    tipoVisualizacion: String,
    viewModel: InscripcionViewModel,
    usuarioViewModel: UsuarioViewModel,
    navController: NavHostController, // <- AÑADIDO
    onBack: () -> Unit
) {
    // Obtenemos las inscripciones según el tipo de fecha seleccionado
    val inscripciones by remember(fecha, tipoVisualizacion) {
        when (tipoVisualizacion) {
            "pago" -> viewModel.obtenerInscripcionesPorFechaPago(fecha)
            "inscripcion" -> viewModel.obtenerInscripcionesPorFechaInscripcion(fecha)
            "vencimiento" -> viewModel.obtenerInscripcionesPorFechaVencimiento(fecha)
            else -> viewModel.obtenerInscripcionesPorFechaPago(fecha)
        }
    }.collectAsState(initial = emptyList())

    val usuarios by usuarioViewModel.usuarios.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Volver")
            }
            Text("Inscripciones del ${fecha.dayOfMonth}/${fecha.monthValue}/${fecha.year}")
        }

        Spacer(Modifier.height(8.dp))

        if (inscripciones.isEmpty()) {
            Text("No hay inscripciones este día.")
        } else {
            LazyColumn {
                items(inscripciones.size) { index ->
                    val inscripcion = inscripciones[index]
                    val usuario = usuarios.find { it.id == inscripcion.idUsuario }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                usuario?.id?.let {
                                    navController.navigate("usuario_detalle/$it")
                                }
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text("Nombre: ${usuario?.nombre ?: "Desconocido"}")
                            Text("Vencimiento: ${inscripcion.fechaVencimiento}")
                            Text("Inscripción: ${inscripcion.fechaInscripcion}")
                            Text("Pago: ${inscripcion.fechaPago}")
                            Text(if (inscripcion.pagado) "Pagado" else "Falta pago")
                        }
                    }
                }
            }
        }
    }
}