package com.example.gimnasio.ui

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

//SHA1: 04:F2:45:4F:D1:39:82:E8:84:74:A4:29:B6:44:54:31:E7:BC:AB:B0
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavHostController) {
    // Estado para el modo oscuro/claro
    var darkThemeEnabled by remember { mutableStateOf(false) }

    // Estado para notificaciones
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Estado para el selector de gráficos
    var selectedChartType by remember { mutableStateOf("Usuarios nuevos") }

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
                    text = "admin@calabozogym.com",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GymMediumGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección de Configuración
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
                // Opción de tema oscuro
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tema oscuro",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = GymDarkBlue
                        )
                    )
                    Switch(
                        checked = darkThemeEnabled,
                        onCheckedChange = { darkThemeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GymBrightRed,
                            checkedTrackColor = GymBrightRed.copy(alpha = 0.5f),
                            uncheckedThumbColor = GymMediumGray,
                            uncheckedTrackColor = GymMediumGray.copy(alpha = 0.5f)
                        )
                    )
                }

                Divider(color = GymLightGray, thickness = 1.dp)

                // Opción de notificaciones
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
        // Sección de Acciones
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
                // Exportar datos
                ListItem(
                    headlineContent = { Text("Exportar datos") },
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

                // Copia de seguridad
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
                        Text(
                            "Cerrar sesión",
                            color = GymBrightRed
                        )
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = "Cerrar sesión",
                            tint = GymBrightRed
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = GymWhite),
                    modifier = Modifier.clickable { /* Lógica de logout */ }
                )
            }
        }

        // Sección de Estadísticas
//        Text(
//            text = "ESTADÍSTICAS",
//            style = MaterialTheme.typography.labelMedium.copy(
//                color = GymMediumGray,
//                fontWeight = FontWeight.Bold
//            ),
//            modifier = Modifier.padding(vertical = 8.dp)
//        )

//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(12.dp),
//            colors = CardDefaults.cardColors(containerColor = GymWhite)
//        ) {
//            Column {
//                // Selector de tipo de gráfico
//                Text(
//                    text = "Tipo de gráfico",
//                    style = MaterialTheme.typography.bodyMedium.copy(
//                        color = GymDarkGray
//                    ),
//                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    FilterChip(
//                        selected = selectedChartType == "Usuarios nuevos",
//                        onClick = { selectedChartType = "Usuarios nuevos" },
//                        label = { Text("Usuarios") },
//                        colors = FilterChipDefaults.filterChipColors(
//                            selectedContainerColor = GymBrightRed,
//                            selectedLabelColor = GymWhite,
//                            containerColor = GymLightGray,
//                            labelColor = GymDarkBlue
//                        )
//                    )
//
//                    FilterChip(
//                        selected = selectedChartType == "Ventas",
//                        onClick = { selectedChartType = "Ventas" },
//                        label = { Text("Ventas") },
//                        colors = FilterChipDefaults.filterChipColors(
//                            selectedContainerColor = GymBrightRed,
//                            selectedLabelColor = GymWhite,
//                            containerColor = GymLightGray,
//                            labelColor = GymDarkBlue
//                        )
//                    )
//
//                    FilterChip(
//                        selected = selectedChartType == "Asistencias",
//                        onClick = { selectedChartType = "Asistencias" },
//                        label = { Text("Asistencias") },
//                        colors = FilterChipDefaults.filterChipColors(
//                            selectedContainerColor = GymBrightRed,
//                            selectedLabelColor = GymWhite,
//                            containerColor = GymLightGray,
//                            labelColor = GymDarkBlue
//                        )
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Gráfico de ejemplo (deberías integrar una librería como MPAndroidChart o Victory Native)
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .background(GymLightGray.copy(alpha = 0.3f))
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "Gráfico de $selectedChartType",
//                        style = MaterialTheme.typography.bodyMedium.copy(
//                            color = GymMediumGray
//                        )
//                    )
//                }
//
//                // Botón para ver más estadísticas
//                TextButton(
//                    onClick = { navController.navigate("estadisticas_detalladas") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp)
//                ) {
//                    Text("Ver estadísticas completas")
//                }
//            }
//        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}