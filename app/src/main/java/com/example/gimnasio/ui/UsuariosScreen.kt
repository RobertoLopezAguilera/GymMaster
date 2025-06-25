package com.example.gimnasio.ui

import com.example.gimnasio.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gimnasio.viewmodel.UsuarioViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import com.example.gimnasio.data.model.Usuario
import com.example.gimnasio.ui.theme.*

@Composable
fun UsuariosScreen(
    viewModel: UsuarioViewModel = viewModel(),
    navController: NavHostController
) {
    val usuarios by viewModel.usuarios.collectAsState()
    var query by remember { mutableStateOf("") }
    val usuariosFiltrados = if (query.isBlank()) usuarios
    else usuarios.filter {
        it.nombre?.contains(query, ignoreCase = true) == true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
    ) {
        // Barra de búsqueda mejorada
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        // Lista de usuarios mejorada
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(usuariosFiltrados.size) { index ->
                val usuario = usuariosFiltrados[index]
                UsuarioCard(
                    usuario = usuario,
                    onClick = {
                        navController.navigate("usuario_detalle/${usuario.id}")
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Buscar Usuario...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = GymDarkBlue
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Limpiar búsqueda",
                    modifier = Modifier.clickable { onQueryChange("") },
                    tint = GymDarkBlue
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GymWhite,
            unfocusedContainerColor = GymWhite,
            disabledContainerColor = GymWhite,
            focusedIndicatorColor = GymBrightRed,
            unfocusedIndicatorColor = GymMediumBlue,
        ),
        modifier = modifier
    )
}

@Composable
fun UsuarioCard(
    usuario: Usuario,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GymWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        border = BorderStroke(1.dp, GymMediumBlue.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar del usuario con iniciales
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(GymMediumBlue, GymDarkBlue)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${usuario.nombre?.firstOrNull() ?: 'U'}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GymWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre completo
                Text(
                    text = "${usuario.nombre ?: "Nombre no especificado"}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GymDarkBlue
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Detalles en fila
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Edad
                    InfoChip(
                        icon = painterResource(id = R.drawable.ic_person),
                        text = "${usuario.edad ?: "?"} años",
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    // Genero
                    InfoChip(
                        icon = painterResource(id = R.drawable.ic_gender),
                        text = usuario.genero ?: "Genero. no especificada",
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    // Experiencia
                    InfoChip(
                        icon = painterResource(id = R.drawable.ic_pesas),
                        text = usuario.experiencia ?: "Exp. no especificada",
                        modifier = Modifier.padding(end = 4.dp)
                    )

                }
            }

            // Indicador de estado (si es necesario)
            if (usuario.id.toString() == "") {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Red, shape = CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Icono de flecha
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ver detalle",
                tint = GymMediumBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun InfoChip(
    icon: Painter,
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = GymMediumBlue.copy(alpha = 0.1f),
    iconColor: Color = GymMediumBlue,
    textColor: Color = GymDarkBlue
) {
    Row(
        modifier = modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(12.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}