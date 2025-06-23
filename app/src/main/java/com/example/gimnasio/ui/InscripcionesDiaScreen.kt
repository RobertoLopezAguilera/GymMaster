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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.data.model.Usuario
import com.example.gimnasio.ui.theme.GymLightGray
import com.example.gimnasio.viewmodel.InscripcionViewModel
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun InscripcionesDiaScreen(
    fecha: LocalDate,
    tipoVisualizacion: String,
    viewModel: InscripcionViewModel,
    usuarioViewModel: UsuarioViewModel,
    navController: NavHostController,
    onBack: () -> Unit
) {
    val usuarios by usuarioViewModel.usuarios.collectAsState()

    // Flujos de datos separados
    val inscripcionesFlow = remember(fecha, tipoVisualizacion) {
        when (tipoVisualizacion) {
            "pago" -> viewModel.obtenerInscripcionesPorFechaPago(fecha)
            "vencimiento" -> viewModel.obtenerInscripcionesPorFechaVencimiento(fecha)
            else -> viewModel.obtenerInscripcionesPorFechaPago(fecha)
        }
    }
    val inscripciones by inscripcionesFlow.collectAsState(initial = emptyList())

    val usuariosFiltradosFlow = remember(fecha, tipoVisualizacion) {
        if (tipoVisualizacion == "mes") {
            val mesAnio = "${fecha.year}-${fecha.monthValue.toString().padStart(2, '0')}"
            viewModel.getUsuariosPorFiltro(mesAnio, FilterType.MONTH)
        } else {
            // Flujo vacío para otros casos
            flowOf(emptyList<Usuario>())
        }
    }
    val usuariosFiltradosPorMes by usuariosFiltradosFlow.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
    ) {
        // Header con botón de volver
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Volver"
                )
            }

            // Título dinámico
            Text(
                text = if (tipoVisualizacion == "mes") {
                    "Inscripciones de ${fecha.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${fecha.year}"
                } else {
                    "Inscripciones del ${fecha.dayOfMonth}/${fecha.monthValue}/${fecha.year}"
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        // Mostrar resumen para vista mensual
        if (tipoVisualizacion == "mes") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Total usuarios: ${usuariosFiltradosPorMes.size}",
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        // Lógica de visualización
        if (tipoVisualizacion == "mes") {
            // Vista mensual - mostrar usuarios filtrados por mes
            if (usuariosFiltradosPorMes.isEmpty()) {
                Text("No hay inscripciones este mes.")
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(usuariosFiltradosPorMes.size) { index ->
                        val usuario = usuariosFiltradosPorMes[index]
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
        } else {
            // Vista diaria - lógica original
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
}