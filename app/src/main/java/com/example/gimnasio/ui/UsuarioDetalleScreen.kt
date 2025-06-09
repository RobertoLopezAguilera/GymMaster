package com.example.gimnasio.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.gimnasio.viewmodel.UsuarioDetalleViewModel

@Composable
fun UsuarioDetalleScreen(
    usuarioId: Int,
    navController: NavController,
    viewModel: UsuarioDetalleViewModel = viewModel()
) {
    val usuario by viewModel.getUsuario(usuarioId).collectAsState(initial = null)
    val inscripcion by viewModel.getInscripcion(usuarioId).collectAsState(initial = null)
    val membresiaFlow = inscripcion?.idMembresia?.let { viewModel.getMembresia(it) }
    val membresiaState = membresiaFlow?.collectAsState(initial = null)
    val membresia = membresiaState?.value

    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(2.dp)) {
            usuario?.let { user ->
                Text("${user.nombre}", style = MaterialTheme.typography.headlineSmall)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Edad: ${user.edad} años", style = MaterialTheme.typography.bodyLarge)
                            Text("Peso: ${user.peso} kg", style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${user.genero}", style = MaterialTheme.typography.bodyLarge)
                            Text("${user.experiencia}  ", style = MaterialTheme.typography.bodyLarge)
                        }

                    }
                }
            }

            Spacer(Modifier.height(15.dp))
            Text("Inscripción", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))

            val borderColor = if (inscripcion?.pagado == true) Color(0xFF4CAF50) else Color(0xFFF44336) // Verde o Rojo

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    inscripcion?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Inicio: ${it.fechaInscripcion}", style = MaterialTheme.typography.bodyMedium)
                            Text("Fin: ${it.fechaVencimiento}", style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Pago: ${it.fechaPago}", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = if (it.pagado) "✅ Pagado" else "❌ No pagado",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (it.pagado) Color(0xFF4CAF50) else Color(0xFFF44336),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    } ?: Text("Sin inscripción", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(15.dp))
            Text("Membresía", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))

            membresia?.let {
                Card(modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tipo: ${it.tipo}")
                        Text("Precio: ${it.costo} MXN")
                        Text("Duración: ${it.duracionDias} días")
                    }
                }
            }
        }

        // FABs flotantes
        Box(modifier = Modifier.fillMaxSize()) {
            FabMenu(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp), // opcional, para separar del borde
                onEditarClick = { navController.navigate("editar_usuario/$usuarioId") },
                onAsignarMembresiaClick = { navController.navigate("asignar_membresia/$usuarioId") },
                onPagarClick = { navController.navigate("pagar-usuario/$usuarioId") },
                onEliminarClick = { showDialog = true }
            )
        }

    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar este usuario y toda su información asociada?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModel.eliminarUsuarioConTodo(usuarioId) {
                        navController.popBackStack()
                    }
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

}
