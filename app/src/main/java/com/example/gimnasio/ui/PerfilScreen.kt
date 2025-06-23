package com.example.gimnasio.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gimnasio.ui.theme.*
import com.example.gimnasio.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavHostController) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val photoUrl = user?.photoUrl?.toString()
    val correo = user?.email ?: "No logueado"

    // Estado para mostrar diálogo
    var mostrarDialogoEstadisticas by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
            .padding(16.dp)
    ) {
        // Encabezado del perfil
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (photoUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(photoUrl),
                    contentDescription = "Perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GymMediumBlue)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(GymMediumBlue, CircleShape)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person_perfil),
                        contentDescription = "Perfil",
                        tint = GymWhite,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Administrador",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GymDarkBlue
                    )
                )
                Text(
                    text = correo,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GymMediumGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CONFIGURACIÓN
        Text(
            text = "CONFIGURACIÓN",
            style = MaterialTheme.typography.labelMedium.copy(
                color = GymMediumGray,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GymWhite)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Notificaciones",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = GymDarkBlue
                        )
                    )
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GymBrightRed,
                            checkedTrackColor = GymBrightRed.copy(alpha = 0.5f),
                            uncheckedThumbColor = GymMediumGray,
                            uncheckedTrackColor = GymMediumGray.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ACCIONES
        Text(
            text = "ACCIONES",
            style = MaterialTheme.typography.labelMedium.copy(
                color = GymMediumGray,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GymWhite)
        ) {
            Column {
                // Estadísticas con diálogo
                ListItem(
                    headlineContent = { Text("Estadísticas del Gimnasio") },
                    supportingContent = { Text("Gráficas y más") },
                    leadingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_stats),
                            contentDescription = "Estadísticas",
                            tint = GymDarkBlue
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = GymWhite),
                    modifier = Modifier.clickable {
                        mostrarDialogoEstadisticas = true
                    }
                )

                Divider(color = GymWhite, thickness = 1.dp)

                // Exportar datos
                ListItem(
                    headlineContent = {
                        Text("Exportar datos", color = GymDarkBlue)
                    },
                    supportingContent = { Text("Generar reporte en PDF/Excel") },
                    leadingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_export),
                            contentDescription = "Exportar",
                            tint = GymDarkBlue
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = GymWhite),
                    modifier = Modifier.clickable { /* Exportar lógica */ },
                )

                Divider(color = GymWhite, thickness = 1.dp)

                // Backup
                ListItem(
                    headlineContent = { Text("Copia de seguridad") },
                    supportingContent = { Text("Guardar en la nube o dispositivo") },
                    leadingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_backup),
                            contentDescription = "Backup",
                            tint = GymDarkBlue
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = GymWhite),
                    modifier = Modifier.clickable { /* Backup lógica */ }
                )

                Divider(color = GymWhite, thickness = 1.dp)

                // Cerrar sesión
                ListItem(
                    headlineContent = {
                        Text("Cerrar sesión", color = GymBrightRed)
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = "Cerrar sesión",
                            tint = GymBrightRed
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = GymWhite),
                    modifier = Modifier.clickable {
                        FirebaseAuth.getInstance().signOut()
                        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                        sharedPreferences.edit().remove("USER_EMAIL").apply()
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                )
            }
        }

        // Diálogo de selección de estadísticas
        if (mostrarDialogoEstadisticas) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { mostrarDialogoEstadisticas = false },
                title = { Text("Selecciona una categoría") },
                text = { Text("¿Qué estadísticas deseas ver?") },
                confirmButton = {
                    Text(
                        "Usuarios",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                mostrarDialogoEstadisticas = false
                                navController.navigate("estadisticas_usuarios")
                            }
                    )
                },
                dismissButton = {
                    Text(
                        "Inscripciones",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                mostrarDialogoEstadisticas = false
                                navController.navigate("estadisticas_inscripciones")
                            }
                    )
                }
            )
        }
    }
}
