package com.example.gimnasio.ui

import androidx.compose.foundation.BorderStroke
import com.example.gimnasio.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.gimnasio.ui.theme.*
import com.example.gimnasio.viewmodel.InscripcionViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarioScreen(
    navController: NavHostController,
    viewModel: InscripcionViewModel = viewModel(),
    usuarioViewModel: UsuarioViewModel = viewModel()
) {
    var tipoVisualizacion by remember { mutableStateOf("pago") } // pago, inscripcion, vencimiento
    var mesActual by remember { mutableStateOf(YearMonth.now()) }
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

    val inscripcionesFlow = viewModel.inscripcionesPorTipo(tipoVisualizacion)


    val inscripcionesPorDia by inscripcionesFlow.collectAsState(initial = emptyMap())


    // Preparar días del mes
    val primerDiaSemana = mesActual.atDay(1).dayOfWeek.value % 7
    val diasInicioVacios = List(primerDiaSemana) { null }
    val diasDelMes = (1..mesActual.lengthOfMonth()).map { dia -> mesActual.atDay(dia) }
    val diasCompletos = diasInicioVacios + diasDelMes

    // Colores basados en el tipo de visualización
    val colorPrincipal = when (tipoVisualizacion) {
        "pago" -> GymSecondary
        "vencimiento" -> GymBrightRed
        else -> GymMediumBlue
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
            .padding(horizontal = 10.dp)
    ) {
        // Header con selector de vista
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ordernar por",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = GymDarkBlue,
                    fontWeight = FontWeight.Bold
                )
            )

            // Selector de tipo de visualización
            Box {
                var menuExpandido by remember { mutableStateOf(false) }

                OutlinedButton(
                    onClick = { menuExpandido = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = GymWhite,
                        contentColor = colorPrincipal
                    ),
                    border = BorderStroke(1.dp, colorPrincipal.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = when (tipoVisualizacion) {
                            "pago" -> "Pagos"
                            "vencimiento" -> "Vencimientos"
                            else -> "Inscripciones"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter),
                        contentDescription = "Filtrar",
                        tint = colorPrincipal,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false },
                    modifier = Modifier.background(GymWhite)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Pagos",
                                color = if (tipoVisualizacion == "pago") colorPrincipal else GymDarkBlue
                            )
                        },
                        onClick = {
                            tipoVisualizacion = "pago"
                            menuExpandido = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Inscripciones",
                                color = if (tipoVisualizacion == "inscripcion") colorPrincipal else GymDarkBlue
                            )
                        },
                        onClick = {
                            tipoVisualizacion = "inscripcion"
                            menuExpandido = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Vencimientos",
                                color = if (tipoVisualizacion == "vencimiento") colorPrincipal else GymDarkBlue
                            )
                        },
                        onClick = {
                            tipoVisualizacion = "vencimiento"
                            menuExpandido = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navegación por mes
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GymWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { mesActual = mesActual.minusMonths(1) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Mes anterior",
                        tint = GymMediumBlue
                    )
                }

                Text(
                    text = "${mesActual.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${mesActual.year}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GymDarkBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                IconButton(
                    onClick = { mesActual = mesActual.plusMonths(1) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_forward),
                        contentDescription = "Mes siguiente",
                        tint = GymMediumBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Encabezado días de la semana
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("D", "L", "M", "X", "J", "V", "S").forEach { dia ->
                Text(
                    text = dia,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GymDarkGray,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendario
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(diasCompletos.size) { index ->
                val fecha = diasCompletos[index]
                val hoy = LocalDate.now()

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                ) {
                    if (fecha == null) {
                        // Celda vacía para días que no pertenecen al mes
                        Box(modifier = Modifier.fillMaxSize())
                    } else {
                        val total = inscripcionesPorDia[fecha] ?: 0
                        val esHoy = fecha == hoy
                        val esMesActual = fecha.month == mesActual.month

                        // Color de fondo basado en la cantidad
                        val colorFondo = when {
                            total == 0 -> Color.Transparent
                            total < 5 -> colorPrincipal.copy(alpha = 0.2f)
                            total < 10 -> colorPrincipal.copy(alpha = 0.4f)
                            else -> colorPrincipal.copy(alpha = 0.6f)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = if (esHoy) GymMediumBlue.copy(alpha = 0.1f) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { fechaSeleccionada = fecha }
                                .padding(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        color = if (total > 0) colorFondo else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = if (esHoy) 1.dp else 0.dp,
                                        color = GymMediumBlue,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Número del día
                                Text(
                                    text = "${fecha.dayOfMonth}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = when {
                                            !esMesActual -> GymMediumGray
                                            esHoy -> GymWhite
                                            else -> GymDarkBlue
                                        },
                                        fontWeight = if (esHoy) FontWeight.Bold else FontWeight.Normal
                                    )
                                )

                                // Indicador de cantidad
                                if (total > 0) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(
                                                color = colorPrincipal,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$total",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = GymWhite,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoResumen(
    icon: Painter,
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = GymDarkGray
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                color = color,
                fontWeight = FontWeight.Bold
            )
        )
    }
}