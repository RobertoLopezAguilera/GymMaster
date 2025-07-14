package com.example.gimnasio.ui

import android.widget.Toast
import com.example.gimnasio.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.ui.theme.*
import com.example.gimnasio.viewmodel.InscripcionViewModel
import com.example.gimnasio.viewmodel.MembresiasViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun AsignarMembresiaScreen(
    usuarioId: String,
    navController: NavController,
    viewModel: MembresiasViewModel = viewModel(),
    inscripcionViewModel: InscripcionViewModel = viewModel()
) {
    val membresias by viewModel.membresias.collectAsState()
    val context = LocalContext.current
    val hoy = LocalDate.now()

    val ultimaInscripcion by inscripcionViewModel
        .obtenerUltimaInscripcion(usuarioId)
        .collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Selecciona una membresía",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = GymDarkBlue,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(membresias) { _, membresia ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val inscripcionActual = ultimaInscripcion
                            val vencimientoActual = inscripcionActual?.let {
                                LocalDate.parse(it.fechaVencimiento)
                            }

                            if (vencimientoActual != null) {
                                when {
                                    // Caso 1: Ya vencida
                                    vencimientoActual.isBefore(hoy) -> {
                                        val nuevaFechaVencimiento = hoy.plusDays(membresia.duracionDias.toLong())
                                        val nuevaInscripcion = Inscripcion(
                                            idUsuario = usuarioId,
                                            idMembresia = membresia.id,
                                            fechaPago = hoy.toString(),
                                            fechaVencimiento = nuevaFechaVencimiento.toString(),
                                            lastUpdated = System.currentTimeMillis(),
                                            pagado = true
                                        )
                                        inscripcionViewModel.insertarInscripcion(nuevaInscripcion)
                                        Toast.makeText(context, "Nueva inscripción registrada", Toast.LENGTH_SHORT).show()
                                    }

                                    // Caso 2: Activa pero quedan 7 días o menos
                                    ChronoUnit.DAYS.between(hoy, vencimientoActual) <= 7 -> {
                                        val diasRestantes = ChronoUnit.DAYS.between(hoy, vencimientoActual).coerceAtLeast(0)
                                        val nuevaDuracion = membresia.duracionDias + diasRestantes.toInt()
                                        val nuevaFechaVencimiento = hoy.plusDays(nuevaDuracion.toLong())

                                        val inscripcionActualizada = Inscripcion(
                                            id = inscripcionActual!!.id, // ✅ ya está seguro que no es null aquí
                                            idUsuario = usuarioId,
                                            idMembresia = membresia.id,
                                            fechaPago = hoy.toString(),
                                            fechaVencimiento = nuevaFechaVencimiento.toString(),
                                            lastUpdated = System.currentTimeMillis(),
                                            pagado = true
                                        )

                                        inscripcionViewModel.actualizarIncripcion(inscripcionActualizada)
                                        Toast.makeText(context, "Inscripción actualizada y extendida", Toast.LENGTH_SHORT).show()
                                    }

                                    // Caso 3: Inscripción activa con más de 7 días — no hacer nada
                                    else -> {
                                        Toast.makeText(context, "La inscripción actual aún está activa", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                // Caso 4: No hay inscripción previa
                                val nuevaFechaVencimiento = hoy.plusDays(membresia.duracionDias.toLong())
                                val nuevaInscripcion = Inscripcion(
                                    idUsuario = usuarioId,
                                    idMembresia = membresia.id,
                                    fechaPago = hoy.toString(),
                                    fechaVencimiento = nuevaFechaVencimiento.toString(),
                                    lastUpdated = System.currentTimeMillis(),
                                    pagado = true
                                )
                                inscripcionViewModel.insertarInscripcion(nuevaInscripcion)
                                Toast.makeText(context, "Primera inscripción registrada", Toast.LENGTH_SHORT).show()
                            }

                            navController.popBackStack()
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GymWhite),
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = BorderStroke(1.dp, GymMediumBlue.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = membresia.tipo ?: "Membresía",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = GymDarkBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Chip(
                                    text = "$ ${membresia.costo}",
                                    backgroundColor = GymSecondary.copy(alpha = 0.1f),
                                    textColor = GymSecondary,
                                    icon = painterResource(id = R.drawable.ic_payments)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Chip(
                                    text = "${membresia.duracionDias} días",
                                    backgroundColor = GymMediumBlue.copy(alpha = 0.1f),
                                    textColor = GymMediumBlue,
                                    icon = painterResource(id = R.drawable.ic_calendario)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

