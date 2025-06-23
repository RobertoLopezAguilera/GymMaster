package com.example.gimnasio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gimnasio.ui.theme.*
import com.example.gimnasio.viewmodel.UsuarioDetalleViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext

@Composable
fun EstadisticasUsuariosScreen(
    navController: NavHostController,
    viewModel: UsuarioDetalleViewModel = viewModel()
) {
    val meses = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    val mesesMap = meses.mapIndexed { index, mes -> mes to String.format("%02d", index + 1) }.toMap()
    val añosDisponibles = (2023..2025).map { it.toString() }

    // ✅ Por defecto: FILTRAR POR AÑO
    var filtroTipo by remember { mutableStateOf(FilterType.YEAR) }
    var filtroAño by remember { mutableStateOf("2025") }
    var filtroMes by remember { mutableStateOf("06") }

    val filtro = if (filtroTipo == FilterType.MONTH) "$filtroAño-$filtroMes" else filtroAño
    val usuarios by viewModel.getUsuariosPorFiltro(filtro, filtroTipo).collectAsState(initial = emptyList())

    // ✅ Recalcular datos de gráfica cuando cambian filtros o usuarios
    val datosGrafica by remember(filtroTipo, usuarios) {
        derivedStateOf {
            when (filtroTipo) {
                FilterType.MONTH -> {
                    usuarios.groupBy { it.fechaInscripcion }
                        .map { (fecha, lista) -> fecha.substring(8) to lista.size.toFloat() }
                        .sortedBy { it.first }
                }

                FilterType.YEAR -> {
                    // Agrupar por mes y llenar todos los meses
                    val porMes = usuarios.groupBy { it.fechaInscripcion.substring(5, 7) }
                        .mapValues { it.value.size.toFloat() }

                    (1..12).map { mesNum ->
                        val mesStr = String.format("%02d", mesNum)
                        val nombreMes = meses[mesNum - 1]
                        nombreMes to (porMes[mesStr] ?: 0f)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
            .padding(16.dp)
    ) {
        Text(
            text = "Estadísticas de Usuarios",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GymDarkBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de tipo de filtro
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Filtrar por:", color = GymDarkBlue, modifier = Modifier.padding(end = 8.dp))
            DropdownMenuFiltroTipo(filtroTipo) { filtroTipo = it }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Selección de año y mes si aplica
        Row(verticalAlignment = Alignment.CenterVertically) {
            DropdownMenuAño(filtroAño, añosDisponibles) { filtroAño = it }
            Spacer(modifier = Modifier.width(8.dp))
            if (filtroTipo == FilterType.MONTH) {
                DropdownMenuMes(filtroMes, mesesMap) { filtroMes = it }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta resumen
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GymWhite),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Usuarios Totales", color = GymMediumGray)
                Text(
                    usuarios.size.toString(),
                    style = MaterialTheme.typography.headlineSmall.copy(color = GymDarkBlue)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            if (filtroTipo == FilterType.MONTH) "Inscripciones por Día" else "Inscripciones por Mes",
            color = GymDarkBlue,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        BarChartCard(
            data = datosGrafica,
            barColor = GymMediumBlue,
            modifier = Modifier.height(300.dp)
        )
    }
}

@Composable
fun BarChartCard(
    data: List<Pair<String, Float>>,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(GymWhite, RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay datos disponibles", color = GymMediumGray)
        }
        return
    }

    val context = LocalContext.current
    val barChart = remember { BarChart(context) }

    // Actualiza los datos cuando cambian
    LaunchedEffect(data) {
        val labels = data.map { it.first }
        val entries = data.mapIndexed { index, (_, value) ->
            BarEntry(index.toFloat(), value)
        }

        val dataSet = BarDataSet(entries, "Inscripciones").apply {
            color = barColor.toArgb()
            valueTextColor = Color.Black.toArgb()
            valueTextSize = 12f
            setDrawValues(true)
        }

        barChart.apply {
            this.data = BarData(dataSet).apply {
                barWidth = 0.5f
            }

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                labelRotationAngle = -45f
                textSize = 12f
                granularity = 1f
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
            }

            axisLeft.apply {
                axisMinimum = 0f
                granularity = 1f
                setDrawGridLines(true)
            }

            axisRight.isEnabled = false
            legend.isEnabled = false

            notifyDataSetChanged()
            invalidate()
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GymWhite),
        modifier = modifier.fillMaxWidth()
    ) {
        AndroidView(
            factory = { barChart },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}


@Composable
fun DropdownMenuFiltroTipo(
    selected: FilterType,
    onSelected: (FilterType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Text(
            text = if (selected == FilterType.MONTH) "Mes" else "Año",
            modifier = Modifier
                .background(GymWhite, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(8.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Mes") }, onClick = {
                onSelected(FilterType.MONTH)
                expanded = false
            })
            DropdownMenuItem(text = { Text("Año") }, onClick = {
                onSelected(FilterType.YEAR)
                expanded = false
            })
        }
    }
}

@Composable
fun DropdownMenuAño(selected: String, options: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Text(
            text = selected,
            modifier = Modifier
                .background(GymWhite, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(8.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = {
                    onSelected(it)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun DropdownMenuMes(selectedMes: String, mesesMap: Map<String, String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val mesNombre = mesesMap.entries.firstOrNull { it.value == selectedMes }?.key ?: "Mes"

    Box {
        Text(
            text = mesNombre,
            modifier = Modifier
                .background(GymWhite, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(8.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            mesesMap.forEach { (nombre, valor) ->
                DropdownMenuItem(text = { Text(nombre) }, onClick = {
                    onSelected(valor)
                    expanded = false
                })
            }
        }
    }
}

enum class FilterType {
    MONTH,
    YEAR
}