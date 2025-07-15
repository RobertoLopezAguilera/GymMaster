package com.example.gimnasio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gimnasio.R
import com.example.gimnasio.admob.AdBanner
import com.example.gimnasio.ui.theme.GymBrightRed
import com.example.gimnasio.ui.theme.GymDarkBlue
import com.example.gimnasio.ui.theme.GymDarkGray
import com.example.gimnasio.ui.theme.GymLightGray
import com.example.gimnasio.ui.theme.GymMediumBlue
import com.example.gimnasio.ui.theme.GymSecondary
import com.example.gimnasio.ui.theme.GymWhite
import com.example.gimnasio.viewmodel.UsuarioDetalleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InscripcionesScreen(
    viewModel: UsuarioDetalleViewModel = viewModel(),
    navController: NavHostController
) {
    val inscripciones by viewModel.getAllUsuarios2().collectAsState(initial = emptyList())
    val usuariosMap by viewModel.allUsuarios.collectAsState(initial = emptyMap())
    var query by remember { mutableStateOf("") }

    // Filtrado optimizado usando el mapa de usuarios
    val inscripcionesFiltradas by remember(inscripciones, usuariosMap, query) {
        derivedStateOf {
            if (query.isBlank()) {
                inscripciones
            } else {
                inscripciones.filter { inscripcion ->
                    usuariosMap[inscripcion.idUsuario]?.nombre
                        ?.contains(query, ignoreCase = true) ?: false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                TopAppBar(
                    title = { Text("Historial de todos los Pagos", color = GymDarkBlue) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_back),
                                contentDescription = "Regresar",
                                tint = GymDarkBlue
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = GymLightGray,
                        titleContentColor = GymDarkBlue,
                        navigationIconContentColor = GymDarkBlue
                    )
                )
            }
        },
        bottomBar = {
            // Banner en la parte inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(GymLightGray)
            ) {
                AdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
        }
    ) { paddingValues ->
        if (inscripciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GymLightGray)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay registros de pago")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GymLightGray)
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f) // Ocupa todo el espacio disponible
                        .padding(bottom = 8.dp) // Espacio para el banner
                ) {
                    items(inscripcionesFiltradas.size) { index ->
                        val inscripcion = inscripcionesFiltradas[index]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = GymWhite),
                            elevation = CardDefaults.cardElevation(4.dp),
                            border = BorderStroke(1.dp, GymMediumBlue.copy(alpha = 0.3f))
                        ) {
                            // Obtenemos el usuario directamente del mapa
                            val usuario = usuariosMap[inscripcion.idUsuario]

                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = usuario?.nombre ?: "Nombre no especificado",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GymDarkBlue
                                    )
                                )

                                // Resto del código de la tarjeta...
                                InfoRow(
                                    icon = painterResource(id = R.drawable.ic_check_green),
                                    label = "Vencimiento",
                                    value = formatearFecha(inscripcion.fechaVencimiento),
                                    valueColor = if (inscripcion.pagado) GymDarkBlue else GymBrightRed
                                )

                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                InfoRow(
                                    icon = painterResource(id = R.drawable.ic_payments),
                                    label = "Último pago",
                                    value = formatearFecha(inscripcion.fechaPago),
                                    valueColor = if (inscripcion.pagado) GymDarkBlue else GymBrightRed
                                )

                                Divider(modifier = Modifier.padding(vertical = 8.dp))

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
                                    val fechaVencimiento = LocalDate.parse(inscripcion.fechaVencimiento, formatter)
                                    val estaActiva = !fechaVencimiento.isBefore(hoy)

                                    Chip(
                                        text = if (estaActiva) "ACTIVO" else "VENCIDO",
                                        backgroundColor = if (estaActiva) GymSecondary.copy(alpha = 0.2f)
                                        else GymBrightRed.copy(alpha = 0.2f),
                                        textColor = if (estaActiva) GymSecondary else GymBrightRed,
                                        icon = if (estaActiva) Icons.Default.Check else Icons.Default.Close
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