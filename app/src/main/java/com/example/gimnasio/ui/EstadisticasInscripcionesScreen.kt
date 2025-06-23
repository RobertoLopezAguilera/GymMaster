package com.example.gimnasio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.gimnasio.ui.theme.*

@Composable
fun EstadisticasInscripcionesScreen(navController: NavHostController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
            .padding(16.dp)
    ) {
        Text(
            text = "Estadísticas de Inscripciones",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GymDarkBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total de inscripciones este mes
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GymWhite),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Inscripciones este mes", color = GymMediumGray)
                Text("25", style = MaterialTheme.typography.headlineSmall.copy(color = GymDarkBlue))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lugar para una grafica de ganancias
        Text("Ganancias por Semana", color = GymDarkBlue, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(GymWhite, RoundedCornerShape(12.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("[ Gráfica aquí ]", color = GymMediumGray)
        }
    }
}
