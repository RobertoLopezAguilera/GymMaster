package com.example.gimnasio.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.gimnasio.R
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.ui.theme.*
import com.example.gimnasio.viewmodel.UsuarioDetalleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun UsuarioDetalleScreen(
    usuarioId: String,
    navController: NavController,
    viewModel: UsuarioDetalleViewModel = viewModel()
) {
    val usuario by viewModel.getUsuario(usuarioId.toString()).collectAsState(initial = null)
    val inscripcion by viewModel.getInscripcion(usuarioId.toString()).collectAsState(initial = null)
    val membresiaFlow = inscripcion?.idMembresia?.let { viewModel.getMembresia(it) }
    val membresiaState = membresiaFlow?.collectAsState(initial = null)
    val membresia = membresiaState?.value
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Header con avatar y nombre
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar del usuario
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(GymMediumBlue, GymDarkBlue)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${usuario?.nombre?.firstOrNull() ?: 'U'}",
                            style = MaterialTheme.typography.displaySmall.copy(
                                color = GymWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        usuario?.let { user ->
                            Text(
                                text = user.nombre ?: "Nombre no especificado",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GymDarkBlue
                                )
                            )
                            Text(
                                text = "Miembro desde: ${formatearFecha(user.fechaInscripcion) ?: "Fecha desconocida"}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GymDarkGray
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección de información personal
                Text(
                    text = "Información Personal",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GymDarkBlue,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GymWhite),
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = BorderStroke(1.dp, GymMediumBlue.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        usuario?.let { user ->
                            InfoRow(
                                icon = painterResource(id = R.drawable.ic_calendario),
                                label = "Edad",
                                value = "${user.edad ?: "?"} años"
                            )

                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = GymLightGray.copy(alpha = 0.5f)
                            )

                            InfoRow(
                                icon = painterResource(id = R.drawable.ic_weight),
                                label = "Peso",
                                value = "${user.peso ?: "?"} kg"
                            )

                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = GymLightGray.copy(alpha = 0.5f)
                            )

                            InfoRow(
                                icon = painterResource(id = R.drawable.ic_gender),
                                label = "Género",
                                value = user.genero ?: "No especificado"
                            )

                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = GymLightGray.copy(alpha = 0.5f)
                            )

                            InfoRow(
                                icon = painterResource(id = R.drawable.ic_membresia),
                                label = "Experiencia",
                                value = user.experiencia ?: "No especificada"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección de inscripción
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val hoy = LocalDate.now()

                // Calcular si está pagado basado en la fecha
                val estaPagado = inscripcion?.fechaVencimiento?.let { fechaVenc ->
                    val fechaVencimiento = LocalDate.parse(fechaVenc, formatter)
                    !fechaVencimiento.isBefore(hoy)
                } ?: false

                // Cambiar los colores basados en el estado calculado
                val borderColor = if (estaPagado) GymSecondary else GymBrightRed

                Text(
                    text = "Suscripción",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GymDarkBlue,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GymWhite),
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = BorderStroke(1.dp, borderColor.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        inscripcion?.let {
                            InfoRow(
                                icon = painterResource(id = R.drawable.ic_check_green),
                                label = "Vencimiento",
                                value = formatearFecha(it.fechaVencimiento),
                                valueColor = if (estaPagado) GymDarkBlue else GymBrightRed
                            )

                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = GymLightGray.copy(alpha = 0.5f)
                            )

                            InfoRow(
                                icon = painterResource(id = R.drawable.ic_payments),
                                label = "Último pago",
                                value = formatearFecha(it.fechaPago),
                                valueColor = if (estaPagado) GymDarkBlue else GymBrightRed
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

                Spacer(modifier = Modifier.height(24.dp))

                // Sección de membresía
                Text(
                    text = "Tipo de Membresía",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GymDarkBlue,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                membresia?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GymWhite),
                        elevation = CardDefaults.cardElevation(4.dp),
                        border = BorderStroke(1.dp, GymMediumBlue.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            InfoRow(
                                icon = painterResource(id = R.drawable.ic_membresia),
                                label = "Tipo",
                                value = it.tipo ?: "No especificado"
                            )

                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = GymLightGray.copy(alpha = 0.5f)
                            )

                            InfoRow(
                                icon = painterResource(id = R.drawable.ic_price),
                                label = "Precio",
                                value = "${it.costo ?: "?"} MXN"
                            )

                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = GymLightGray.copy(alpha = 0.5f)
                            )

                            InfoRow(
                                icon = painterResource(id = R.drawable.ic_duration),
                                label = "Duración",
                                value = "${it.duracionDias ?: "?"} días"
                            )
                        }
                    }
                } ?: Text(
                    "No tiene membresía asignada",
                    style = MaterialTheme.typography.bodyMedium.copy(color = GymDarkGray),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(80.dp)) // Espacio para los FABs
            }
        }

        var showChangeMembershipDialog by remember { mutableStateOf(false) }

        // Diálogo para confirmar cambio de membresía
        if (showChangeMembershipDialog) {
            AlertDialog(
                onDismissRequest = { showChangeMembershipDialog = false },
                title = {
                    Text("Membresía activa",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = GymDarkBlue,
                            fontWeight = FontWeight.Bold
                        ))
                },
                text = {
                    Text("El usuario ya tiene una membresía activa. " +
                            "¿Estás seguro que deseas cambiar de membresía?",
                        style = MaterialTheme.typography.bodyMedium)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showChangeMembershipDialog = false
                            navController.navigate("asignar_membresia/$usuarioId")
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = GymBrightRed
                        )
                    ) {
                        Text("CAMBIAR", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showChangeMembershipDialog = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = GymMediumBlue
                        )
                    ) {
                        Text("CANCELAR")
                    }
                },
                containerColor = GymWhite,
                shape = RoundedCornerShape(12.dp)
            )
        }

        FabMenu(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            onEditarClick = { navController.navigate("editar_usuario/$usuarioId") },
            onAsignarMembresiaClick = {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val hoy = LocalDate.now()

                inscripcion?.let { insc ->
                    val fechaVencimiento = insc.fechaVencimiento?.let {
                        try {
                            LocalDate.parse(it, formatter)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (fechaVencimiento != null && !fechaVencimiento.isBefore(hoy)) {
                        // Mostrar diálogo de confirmación
                        showChangeMembershipDialog = true
                    } else {
                        // No hay membresía activa o está vencida
                        navController.navigate("asignar_membresia/$usuarioId")
                    }
                } ?: run {
                    // No hay inscripción
                    navController.navigate("asignar_membresia/$usuarioId")
                }
            },
            onPagarClick = {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val hoy = LocalDate.now()

                if (membresia == null) {
                    Toast.makeText(context, "No hay membresía asignada", Toast.LENGTH_SHORT).show()
                    return@FabMenu
                }

                inscripcion?.let { insc ->
                    val fechaVencimiento = LocalDate.parse(insc.fechaVencimiento, formatter)
                    val diasRestantes = ChronoUnit.DAYS.between(hoy, fechaVencimiento)

                    // Si faltan más de 7 días para vencer
                    if (diasRestantes > 7) {
                        Toast.makeText(
                            context,
                            "No puedes renovar. Faltan $diasRestantes días para vencimiento",
                            Toast.LENGTH_LONG
                        ).show()
                        return@FabMenu
                    }
                }

                val fechaActual = hoy.toString()
                val duracionDias = membresia.duracionDias ?: 30

                // Nueva lógica para calcular fecha de vencimiento
                val nuevaFechaVencimiento = if (inscripcion != null) {
                    val fechaVencimientoActual = LocalDate.parse(inscripcion!!.fechaVencimiento, formatter)
                    val diasAtraso = ChronoUnit.DAYS.between(fechaVencimientoActual, hoy)

                    if (diasAtraso > 0 && diasAtraso <= 7) {
                        // Si está en período de gracia (vencido hace menos de 7 días)
                        fechaVencimientoActual.plusDays(duracionDias.toLong()).toString()
                    } else {
                        // Si no está vencido o está vencido hace más de 7 días
                        hoy.plusDays(duracionDias.toLong()).toString()
                    }
                } else {
                    // Si no hay inscripción previa
                    hoy.plusDays(duracionDias.toLong()).toString()
                }

                viewModel.insertarInscripcion(
                    Inscripcion(
                        idUsuario = usuarioId.toString(),
                        idMembresia = membresia.id,
                        fechaPago = fechaActual,
                        fechaVencimiento = nuevaFechaVencimiento,
                        pagado = true
                    )
                )
                Toast.makeText(context, "Pago registrado exitosamente", Toast.LENGTH_SHORT).show()
            },
            onHistorialClick = { navController.navigate("historial_usuario/$usuarioId") },
            onEliminarClick = { showDialog = true }
        )

    }

    // Diálogo de confirmación
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    "Confirmar eliminación",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GymBrightRed,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar este usuario y toda su información asociada?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        viewModel.eliminarUsuarioConTodo(usuarioId.toString()) {
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = GymBrightRed
                    )
                ) {
                    Text("ELIMINAR", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = GymMediumBlue
                    )
                ) {
                    Text("CANCELAR")
                }
            },
            containerColor = GymWhite,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
internal fun InfoRow(
    icon: Painter,
    label: String,
    value: String,
    valueColor: Color = GymDarkBlue
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = GymMediumBlue,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = GymDarkGray
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = valueColor,
                    fontWeight = FontWeight.Medium
                )
            )
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

@Composable
fun FabMenu(
    modifier: Modifier = Modifier,
    onEditarClick: () -> Unit,
    onAsignarMembresiaClick: () -> Unit,
    onPagarClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    var isMenuOpen by remember { mutableStateOf(false) }
    val transition = updateTransition(isMenuOpen, label = "fabTransition")

    // Animaciones para los botones
    val buttonSpacing by transition.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessMedium) },
        label = "buttonSpacing"
    ) { if (it) 56.dp else 0.dp }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 150) },
        label = "alpha"
    ) { if (it) 1f else 0f }

    val rotation by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 200) },
        label = "rotation"
    ) { if (it) 45f else 0f }

    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End
    ) {
        // Botón Editar
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    isMenuOpen = false
                    onEditarClick()
                },
                modifier = Modifier
                    .height(48.dp)
                    .alpha(alpha),
                containerColor = GymMediumBlue,
                contentColor = GymWhite,
                text = { Text("Editar") },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person_edit),
                        contentDescription = "Editar"
                    )
                }
            )
        }

        // Botón Asignar Membresía
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    isMenuOpen = false
                    onAsignarMembresiaClick()
                },
                modifier = Modifier
                    .height(48.dp)
                    .alpha(alpha),
                containerColor = Color(0xFF2196F3),
                contentColor = GymWhite,
                text = { Text("Membresía") },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendario),
                        contentDescription = "Asignar Membresía"
                    )
                }
            )
        }

        //Botón Pagar
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    isMenuOpen = false
                    onPagarClick()
                },
                modifier = Modifier
                    .height(48.dp)
                    .alpha(alpha),
                containerColor = Color(0xFF4AC250),
                contentColor = GymWhite,
                text = { Text("Pagar") },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_payments),
                        contentDescription = "Pagar"
                    )
                }
            )
        }

        // BotonhHistorial membresias
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    isMenuOpen = false
                    onHistorialClick()
                },
                modifier = Modifier
                    .height(48.dp)
                    .alpha(alpha),
                containerColor = Purple40,
                contentColor = GymWhite,
                text = { Text("Historial") },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_membresia),
                        contentDescription = "Historial"
                    )
                }
            )
        }

        // Botón Eliminar
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            ExtendedFloatingActionButton(
                onClick = {
                    isMenuOpen = false
                    onEliminarClick()
                },
                modifier = Modifier
                    .height(48.dp)
                    .alpha(alpha),
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = GymWhite,
                text = { Text("Eliminar") },
                icon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar Usuario"
                    )
                }
            )
        }

        // Botón principal
        FloatingActionButton(
            onClick = { isMenuOpen = !isMenuOpen },
            modifier = Modifier
                .size(56.dp),
            containerColor = GymBrightRed,
            contentColor = GymWhite,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp
            )
        ) {
            Icon(
                imageVector = if (isMenuOpen) Icons.Default.Close else Icons.Default.MoreVert,
                contentDescription = "Más opciones",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

fun onPagarClick(){

}
