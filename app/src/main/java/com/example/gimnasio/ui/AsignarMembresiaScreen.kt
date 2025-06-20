package com.example.gimnasio.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.ui.theme.*
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
            .background(GymLightGray)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Título mejorado
        Text(
            text = "Selecciona una membresía",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = GymDarkBlue,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Lista de membresías
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(membresias) { _, membresia ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
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
                            navController.popBackStack()
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = GymWhite
                    ),
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = BorderStroke(1.dp, GymMediumBlue.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Tipo de membresía
                        Text(
                            text = membresia.tipo ?: "Membresía",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = GymDarkBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Fila con Precio a la izquierda y Duración a la derecha
                        Row (
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