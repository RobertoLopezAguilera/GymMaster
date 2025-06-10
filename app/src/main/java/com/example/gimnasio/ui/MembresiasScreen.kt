package com.example.gimnasio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.gimnasio.data.model.Membresia
import com.example.gimnasio.ui.theme.*
import com.example.gimnasio.viewmodel.MembresiasViewModel
import com.example.gimnasio.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MembresiasScreen(
    viewModel: MembresiasViewModel = viewModel(),
    navController: NavHostController
) {
    val membresias by viewModel.membresias.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var selectedMembresia by remember { mutableStateOf<Membresia?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
    ) {
        if (membresias.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_membresia),
                    contentDescription = "Sin membresías",
                    tint = GymMediumGray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No hay membresías registradas",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GymDarkGray
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Presiona el botón + para agregar una nueva",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GymMediumGray
                    ),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                itemsIndexed(membresias) { index, membresia ->
                    MembresiaCard(
                        membresia = membresia,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .combinedClickable(
                                onClick = {},// opcion
                                onLongClick = {
                                    selectedMembresia = membresia
                                    showMenu = true
                                }
                            )
                            .animateItemPlacement()
                    )
                }
            }
        }

        // Menú contextual
        if (showMenu && selectedMembresia != null) {
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(GymWhite)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "Editar",
                            style = MaterialTheme.typography.bodyMedium.copy(color = GymDarkBlue)
                        )
                    },
                    onClick = {
                        navController.navigate("editar_membresia/${selectedMembresia!!.id}")
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit_membresia),
                            contentDescription = "Editar",
                            tint = GymMediumBlue
                        )
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            "Eliminar",
                            style = MaterialTheme.typography.bodyMedium.copy(color = GymBrightRed)
                        )
                    },
                    onClick = {
                        showMenu = false
                        showDeleteDialog = true
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = GymBrightRed
                        )
                    }
                )
            }
        }

        // Diálogo de confirmación para eliminar
        if (showDeleteDialog && selectedMembresia != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
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
                        "¿Estás seguro de eliminar la membresía ${selectedMembresia?.tipo}?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.eliminarMembresia(selectedMembresia!!.id)
                            showDeleteDialog = false
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
                        onClick = { showDeleteDialog = false },
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
}

@Composable
fun MembresiaCard(
    membresia: Membresia,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GymWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, GymMediumBlue.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = membresia.tipo,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GymDarkBlue,
                        fontWeight = FontWeight.Bold
                    )
                )

                Chip(
                    text = "${membresia.duracionDias} días",
                    backgroundColor = GymMediumBlue.copy(alpha = 0.1f),
                    textColor = GymMediumBlue,
                    icon = painterResource(id = R.drawable.ic_calendario)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Precio",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GymDarkGray
                        )
                    )
                    Text(
                        text = "$${membresia.costo} MXN",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = GymDarkBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
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
    icon: Painter? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(
                    painter = it,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(14.dp)
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