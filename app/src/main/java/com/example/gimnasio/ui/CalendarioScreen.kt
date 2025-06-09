package com.example.gimnasio.ui

import com.example.gimnasio.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gimnasio.viewmodel.UsuarioViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.gimnasio.viewmodel.InscripcionViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarioScreen(
    navController: NavHostController,
    viewModel: InscripcionViewModel = viewModel(),
    usuarioViewModel: UsuarioViewModel = viewModel()
) {
    var tipoVisualizacion by remember { mutableStateOf("pago") } // pago, inscripcion, vencimiento
    var mesActual by remember { mutableStateOf(YearMonth.now()) }
    var menuExpandido by remember { mutableStateOf(false) }
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }

    // Si hay fecha seleccionada, mostrar pantalla del día
    fechaSeleccionada?.let { fecha ->
        InscripcionesDiaScreen(
            navController = navController,
            fecha = fecha,
            tipoVisualizacion = tipoVisualizacion,
            viewModel = viewModel,
            usuarioViewModel = usuarioViewModel,
            onBack = { fechaSeleccionada = null }
        )

        return
    }

    // Elegir el campo por el cual agrupar
    val inscripcionesPorDia by remember(tipoVisualizacion) {
        when (tipoVisualizacion) {
            "pago" -> viewModel.inscripcionesPor { it.fechaPago }
            "inscripcion" -> viewModel.inscripcionesPor { it.fechaInscripcion }
            "vencimiento" -> viewModel.inscripcionesPor { it.fechaVencimiento }
            else -> viewModel.inscripcionesPor { it.fechaPago }
        }
    }.collectAsState(initial = emptyMap())

    // Preparar días del mes
    val primerDiaSemana = mesActual.atDay(1).dayOfWeek.value % 7
    val diasInicioVacios = List(primerDiaSemana) { null }
    val diasDelMes = (1..mesActual.lengthOfMonth()).map { dia -> mesActual.atDay(dia) }
    val diasCompletos = diasInicioVacios + diasDelMes

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Visualizando por: ${tipoVisualizacion.replaceFirstChar { it.uppercase() }}")
            Box {
                IconButton(onClick = { menuExpandido = true }) {
                    Icon(painter = painterResource(id = R.drawable.ic_options), contentDescription = "Opciones")
                }
                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Ver por Fecha de Pago") },
                        onClick = {
                            tipoVisualizacion = "pago"
                            menuExpandido = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Ver por Fecha de Inscripción") },
                        onClick = {
                            tipoVisualizacion = "inscripcion"
                            menuExpandido = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Ver por Fecha de Vencimiento") },
                        onClick = {
                            tipoVisualizacion = "vencimiento"
                            menuExpandido = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Navegación por mes
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { mesActual = mesActual.minusMonths(1) }) {
                Icon(painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Mes Anterior")
            }
            Text(
                "${mesActual.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${mesActual.year}",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { mesActual = mesActual.plusMonths(1) }) {
                Icon(painter = painterResource(id = R.drawable.ic_arrow_forward), contentDescription = "Mes Siguiente")
            }
        }

        Spacer(Modifier.height(8.dp))

        // Encabezado días de la semana
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.height(4.dp))

        // Calendario
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(diasCompletos.size) { index ->
                val fecha = diasCompletos[index]
                if (fecha == null) {
                    Box(modifier = Modifier.size(48.dp))
                } else {
                    val total = inscripcionesPorDia[fecha] ?: 0
                    val color = when (tipoVisualizacion) {
                        "pago" -> Color.Green.copy(alpha = 0.3f)
                        "inscripcion" -> Color.Blue.copy(alpha = 0.3f)
                        "vencimiento" -> Color.Red.copy(alpha = 0.3f)
                        else -> Color.LightGray
                    }

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .background(
                                if (total > 0) color else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable{
                                fechaSeleccionada = fecha
                            }
                            .padding(8.dp)
                            .size(48.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${fecha.dayOfMonth}")
                            if (total > 0) {
                                Text("↑ $total", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}