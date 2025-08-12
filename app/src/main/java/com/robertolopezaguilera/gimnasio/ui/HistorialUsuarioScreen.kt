package com.robertolopezaguilera.gimnasio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.robertolopezaguilera.gimnasio.viewmodel.InscripcionViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.robertolopezaguilera.gimnasio.R
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.robertolopezaguilera.gimnasio.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialUsuarioScreen(
    usuarioId: String,
    viewModel: InscripcionViewModel = viewModel(),
    navController: NavHostController
) {
    // Obtener todas las inscripciones del usuario
    val inscripciones by viewModel.getByUsuario(usuarioId.toString()).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Historial de Pagos",
                        color = GymDarkBlue // Texto del título
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Regresar",
                            tint = GymDarkBlue // Color del icono
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GymLightGray,     // Fondo del TopAppBar
                    titleContentColor = GymDarkBlue,   // Color del texto
                    navigationIconContentColor = GymDarkBlue
                )
            )
        }
    ) { paddingValues ->
        if (inscripciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GymLightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay registros de pago")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GymLightGray)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(inscripciones.size) { index ->
                        val inscripcion = inscripciones[index]
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GymWhite),
                            elevation = CardDefaults.cardElevation(4.dp),
                            border = BorderStroke(1.dp, GymMediumBlue.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                inscripcion?.let {
                                    InfoRow(
                                        icon = painterResource(id = R.drawable.ic_check_green),
                                        label = "Vencimiento",
                                        value = formatearFecha(it.fechaVencimiento),
                                        valueColor = if (it.pagado) GymDarkBlue else GymBrightRed
                                    )

                                    Divider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = GymLightGray.copy(alpha = 0.5f)
                                    )

                                    InfoRow(
                                        icon = painterResource(id = R.drawable.ic_payments),
                                        label = "Último pago",
                                        value = formatearFecha(it.fechaPago),
                                        valueColor = if (it.pagado) GymDarkBlue else GymBrightRed
                                    )

                                    Divider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = GymLightGray.copy(alpha = 0.5f)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Estado:",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = GymDarkGray
                                            )
                                        )
                                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        val hoy = LocalDate.now()

                                        val fechaVencimiento = LocalDate.parse(it.fechaVencimiento, formatter)

                                        val estaActiva = !fechaVencimiento.isBefore(hoy) // activa si vence hoy o después
                                        Chip(
                                            text = if (estaActiva) "ACTIVO" else "VENCIDO",
                                            backgroundColor = if (estaActiva) GymSecondary.copy(alpha = 0.2f)
                                            else GymBrightRed.copy(alpha = 0.2f),
                                            textColor = if (estaActiva) GymSecondary else GymBrightRed,
                                            icon = if (estaActiva) Icons.Default.Check else Icons.Default.Close
                                        )
                                    }
                                } ?: Text(
                                    "Sin suscripción activa",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = GymDarkGray),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }

                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

        }
    }
}

@Composable
private fun Chip(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    icon: ImageVector? = null
) {
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

fun formatearFecha(fecha: String): String {
    return try {
        val fechaLocal = LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE)
        val mes = fechaLocal.month.getDisplayName(TextStyle.FULL, Locale("es")).lowercase()
        "${fechaLocal.year}-$mes-${fechaLocal.dayOfMonth.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        fecha // si falla, muestra la original
    }
}
