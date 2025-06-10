package com.example.gimnasio.ui

import androidx.compose.foundation.background
import com.example.gimnasio.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.gimnasio.ui.theme.GymLightGray
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
    val usuarios by usuarioViewModel.usuarios.collectAsState()

    val inscripciones by remember(fecha, tipoVisualizacion) {
        when (tipoVisualizacion) {
            "pago" -> viewModel.obtenerInscripcionesPorFechaPago(fecha)
            "vencimiento" -> viewModel.obtenerInscripcionesPorFechaVencimiento(fecha)
            else -> viewModel.obtenerInscripcionesPorFechaPago(fecha)
        }
    }.collectAsState(initial = emptyList())

    val usuariosFiltrados = remember(fecha, tipoVisualizacion, usuarios) {
        if (tipoVisualizacion == "inscripcion") {
            usuarios.filter { it.fechaInscripcion == fecha.toString() }
        } else emptyList()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Volver")
            }
            Text("Inscripciones del dia ${fecha.dayOfMonth}/${fecha.monthValue}/${fecha.year}")
        }

        Spacer(Modifier.height(8.dp))

        if (inscripciones.isEmpty()) {
            Text("No hay inscripciones este día.")
        } else {
            val usuariosDesdeInscripciones = inscripciones.mapNotNull { inscripcion ->
                usuarios.find { it.id == inscripcion.idUsuario }
            }

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(usuariosDesdeInscripciones.size) { index ->
                    val usuario = usuariosDesdeInscripciones[index]
                    UsuarioCard(
                        usuario = usuario,
                        onClick = {
                            navController.navigate("usuario_detalle/${usuario.id}")
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

        }
    }
}