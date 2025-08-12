package com.robertolopezaguilera.gimnasio.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.robertolopezaguilera.gimnasio.ui.theme.*
import com.robertolopezaguilera.gimnasio.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.robertolopezaguilera.gimnasio.admob.AdBanner
import com.robertolopezaguilera.gimnasio.data.AppDatabase
import com.robertolopezaguilera.gimnasio.data.db.FirestoreSyncService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavHostController) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val photoUrl = user?.photoUrl?.toString()
    val correo = user?.email ?: "No logueado"
    val db = remember { AppDatabase.getDatabase(context) }
    val syncService = remember {
        FirestoreSyncService(
            usuarioDao = db.usuarioDao(),
            membresiaDao = db.membresiaDao(),
            inscripcionDao = db.inscripcionDao(),
            context = context
        )
    }
    val scope = rememberCoroutineScope()

    // Estado para mostrar diálogo
    var mostrarDialogoEstadisticas by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            // Banner como bottom bar
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .background(GymLightGray)
                .padding(16.dp)
        ) {
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
                        headlineContent = { Text("Estadísticas del Gimnasio",color = GymDarkBlue) },
                        supportingContent = { Text("Gráficas y más", color = GymDarkBlue) },
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
                        supportingContent = { Text("Generar reporte en PDF/Excel", color = GymDarkBlue) },
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

                    // Backup manual con sincronización
                    var isBackingUp by remember { mutableStateOf(false) }

                    ListItem(
                        headlineContent = { Text("Copia de seguridad", color = GymDarkBlue) },
                        supportingContent = { Text("Guardar en la nube", color = GymDarkBlue) },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_backup),
                                contentDescription = "Backup",
                                tint = GymDarkBlue
                            )
                        },
                        trailingContent = {
                            if (isBackingUp) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = GymDarkBlue,
                                    strokeWidth = 2.dp
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = GymWhite),
                        modifier = Modifier.clickable {
                            if (!isBackingUp) {
                                isBackingUp = true
                                scope.launch {
                                    try {
                                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                                        if (uid == null) {
                                            Toast.makeText(
                                                context,
                                                "Error: Usuario no autenticado",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            return@launch
                                        }

                                        // 1. Mostrar mensaje de inicio
                                        Toast.makeText(context, "Iniciando respaldo y sincronización...", Toast.LENGTH_SHORT).show()

                                        // 2. Realizar respaldo local -> Firestore usando la versión suspend
                                        val backupSuccess = try {
                                            syncService.backupAllWithResult()
                                        } catch (e: Exception) {
                                            false
                                        }

                                        // 3. Sincronizar datos con otros dispositivos solo si el respaldo fue exitoso
                                        if (backupSuccess) {
                                            val syncSuccess = try {
                                                syncService.syncAllDevicesData(uid)
                                            } catch (e: Exception) {
                                                false
                                            }

                                            Toast.makeText(
                                                context,
                                                if (syncSuccess) "Sincronización completada" else "Error en sincronización",
                                                if (syncSuccess) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Error en el respaldo inicial",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Error: ${e.localizedMessage ?: "Error desconocido"}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } finally {
                                        isBackingUp = false
                                    }
                                }
                            }
                        }
                    )

                    Divider(color = GymWhite, thickness = 1.dp)
                    var syncing by remember { mutableStateOf(false) }

                    ListItem(
                        headlineContent = { Text("Sincronizar los datos", color = GymDarkBlue) },
                        supportingContent = { Text("Sincronizar con otros dispositivos", color = GymDarkBlue) },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_backup_restore),
                                contentDescription = "Sincronizacion",
                                tint = GymDarkBlue
                            )
                        },
                        trailingContent = {
                            if (syncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = GymDarkBlue,
                                    strokeWidth = 2.dp
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = GymWhite),
                        modifier = Modifier.clickable(enabled = !syncing) {
                            syncing = true
                            scope.launch {
                                val uid = FirebaseAuth.getInstance().currentUser?.uid
                                if (uid == null) {
                                    context.toast("Usuario no autenticado")
                                    syncing = false
                                    return@launch
                                }

                                context.toast("Iniciando sincronización...")

                                val ok = syncService.twoWaySync(uid)

                                context.toast(if (ok) "Sincronización exitosa" else "Error al sincronizar")
                                syncing = false
                            }
                        }
                    )

                    Divider(color = GymWhite, thickness = 1.dp)

                    //Lista de inscripciones
                    ListItem(
                        headlineContent = { Text("Lista de Inscripciones",
                            color = GymDarkBlue) },
                        supportingContent = { Text("Subscripcion de todos los usuarios",
                            color = GymDarkBlue) },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_membresia),
                                contentDescription = "Inscripciones",
                                tint = GymDarkBlue
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = GymWhite),
                        modifier = Modifier.clickable {
                            navController.navigate("inscripciones_lista")
                        }
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
}

private fun Context.toast(msg: String, long: Boolean = false) {
    Toast.makeText(this, msg, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}
