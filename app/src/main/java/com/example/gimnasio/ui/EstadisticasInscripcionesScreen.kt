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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.gimnasio.R
import com.example.gimnasio.viewmodel.InscripcionViewModel
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EstadisticasInscripcionesScreen(
    navController: NavHostController,
    viewModel: InscripcionViewModel = viewModel()
) {
    val meses = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    val mesesMap = meses.mapIndexed { index, mes -> mes to String.format("%02d", index + 1) }.toMap()

    // Estados para los filtros
    var filtroTipo by remember { mutableStateOf(FilterType.YEAR) }
    var filtroAño by remember { mutableStateOf(LocalDate.now().year.toString()) }
    var filtroMes by remember { mutableStateOf(String.format("%02d", LocalDate.now().monthValue)) }

    // Obtener todas las inscripciones sin filtrar por fecha
    val todasLasInscripciones by viewModel.getAllUsuarios().collectAsState(initial = emptyList())
    val membresias by viewModel.membresias.collectAsState(initial = emptyList())
    val mapMembresias = remember(membresias) {
        membresias.associateBy { it.id }
    }

    // Calcular años disponibles dinámicamente
    val añosDisponibles = remember(todasLasInscripciones) {
        val añosInscripciones = todasLasInscripciones.map {
            LocalDate.parse(it.fechaPago).year
        }
        val minAño = añosInscripciones.minOrNull() ?: LocalDate.now().year
        val maxAño = LocalDate.now().year
        (minAño..maxAño).map { it.toString() }
    }

    // Filtrar inscripciones según los filtros seleccionados
    val inscripcionesFiltradas = remember(todasLasInscripciones, filtroTipo, filtroAño, filtroMes) {
        todasLasInscripciones.filter { inscripcion ->
            val fechaPago = LocalDate.parse(inscripcion.fechaPago)
            when (filtroTipo) {
                FilterType.YEAR -> fechaPago.year.toString() == filtroAño
                FilterType.MONTH -> {
                    fechaPago.year.toString() == filtroAño &&
                            String.format("%02d", fechaPago.monthValue) == filtroMes
                }
            }
        }
    }

    // Filtrar solo inscripciones activas (no vencidas) de las filtradas
    val inscripcionesActivas = remember(inscripcionesFiltradas) {
        inscripcionesFiltradas.filter { inscripcion ->
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val hoy = LocalDate.now()
            val fechaVencimiento = LocalDate.parse(inscripcion.fechaVencimiento, formatter)
            !fechaVencimiento.isBefore(hoy)
        }
    }

    // Estadísticas básicas
    val totalInscripcionesActivas = inscripcionesActivas.size

    // CALCULO CORRECTO DE INGRESOS POR MES (según fechaPago)
    val (ingresosPorMes, totalInscripcionesPorMes) = remember(inscripcionesFiltradas, mapMembresias) {
        val ingresos = mutableMapOf<String, Double>()
        val conteo = mutableMapOf<String, Int>()

        inscripcionesFiltradas.forEach { inscripcion ->
            val mes = inscripcion.fechaPago.substring(5, 7) // Extraer mes (MM) de fechaPago
            val costo = mapMembresias[inscripcion.idMembresia]?.costo ?: 0.0

            ingresos[mes] = (ingresos[mes] ?: 0.0) + costo
            conteo[mes] = (conteo[mes] ?: 0) + 1
        }

        // Ordenar por mes y convertir a lista de pares (nombreMes, valor)
        val ingresosOrdenados = ingresos.toList()
            .sortedBy { it.first } // Ordenar por mes (01, 02, ...)
            .map { (mes, monto) -> meses[mes.toInt() - 1] to monto.toFloat() }

        val conteoOrdenado = conteo.toList()
            .sortedBy { it.first }
            .map { (mes, cantidad) -> meses[mes.toInt() - 1] to cantidad.toFloat() }

        Pair(ingresosOrdenados, conteoOrdenado)
    }

    // Calcular ingresos totales (suma de todos los ingresos por mes)
    val ingresosTotales = remember(ingresosPorMes) {
        ingresosPorMes.sumOf { it.second.toDouble() }
    }

    // Distribución de membresías (inscripciones filtradas)
    val distribucionMembresias by remember(inscripcionesFiltradas, mapMembresias) {
        derivedStateOf {
            inscripcionesFiltradas
                .groupBy { it.idMembresia }
                .map { (idMembresia, lista) ->
                    mapMembresias[idMembresia]?.tipo to lista.size.toFloat()
                }
                .filter { it.first != null }
                .map { it.first!! to it.second }
                .sortedByDescending { it.second }
        }
    }

    // Ingresos por tipo de membresía (inscripciones filtradas)
    val ingresosPorMembresia by remember(inscripcionesFiltradas, mapMembresias) {
        derivedStateOf {
            val map = mutableMapOf<String, Double>()

            inscripcionesFiltradas.forEach { inscripcion ->
                mapMembresias[inscripcion.idMembresia]?.let { membresia ->
                    map[membresia.tipo] = (map[membresia.tipo] ?: 0.0) + membresia.costo
                }
            }

            map.toList().sortedByDescending { it.second }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymLightGray)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Título con botón de retroceso
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Volver atrás",
                    tint = GymDarkBlue
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Estadísticas de Inscripciones",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GymDarkBlue
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filtros de fecha (ahora afectan a todos los gráficos)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Filtrar datos por:", color = GymDarkBlue)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DropdownMenuFiltroTipo(filtroTipo) { filtroTipo = it }
                Spacer(modifier = Modifier.width(8.dp))
                DropdownMenuAño(filtroAño, añosDisponibles) { filtroAño = it }

                if (filtroTipo == FilterType.MONTH) {
                    Spacer(modifier = Modifier.width(8.dp))
                    DropdownMenuMes(filtroMes, mesesMap) { filtroMes = it }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta de resumen financiero
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GymWhite),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Resumen financiero
                Text("Resumen Financiero (${when(filtroTipo) {
                    FilterType.YEAR -> "Año $filtroAño"
                    FilterType.MONTH -> "${meses[filtroMes.toInt() - 1]} $filtroAño"
                }})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = GymDarkBlue,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Ingresos totales (inscripciones filtradas)
                InfoRow(
                    label = "Ingresos totales:",
                    value = "$${"%.2f".format(ingresosTotales)}",
                    valueColor = GymGreen
                )

                // Total de inscripciones
                InfoRow(
                    label = "Total inscripciones:",
                    value = inscripcionesFiltradas.size.toString(),
                    valueColor = GymDarkBlue
                )

                // Usuarios activos actuales
                InfoRow(
                    label = "Usuarios activos:",
                    value = totalInscripcionesActivas.toString(),
                    valueColor = GymDarkBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfico de ingresos mensuales
        Text(
            text = "Ingresos por mes",
            style = MaterialTheme.typography.titleSmall.copy(
                color = GymDarkBlue,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))


        BarChartCard(
            data = ingresosPorMes,
            barColor = GymGreen,
            modifier = Modifier.height(300.dp),
            showValuesAsPercent = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfico de cantidad de inscripciones por mes
        Text(
            text = "Inscripciones por mes",
            style = MaterialTheme.typography.titleSmall.copy(
                color = GymDarkBlue,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        BarChartCard(
            data = totalInscripcionesPorMes,
            barColor = GymMediumBlue,
            modifier = Modifier.height(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfico de distribución de membresías
        Text(
            text = "Distribución de membresías",
            style = MaterialTheme.typography.titleSmall.copy(
                color = GymDarkBlue,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        BarChartMembresias(
            datos = distribucionMembresias,
            modifier = Modifier.height(250.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfico de ingresos por tipo de membresía
        Text(
            text = "Ingresos por tipo de membresía",
            style = MaterialTheme.typography.titleSmall.copy(
                color = GymDarkBlue,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        BarChartIngresosMembresias(
            datos = ingresosPorMembresia.map { (tipo, monto) -> tipo to monto.toFloat() },
            modifier = Modifier.height(250.dp)
        )
    }
}

// Componente para mostrar filas de información
@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = GymDarkBlue) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = GymDarkBlue)
        Text(value, color = valueColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BarChartIngresosMembresias(
    datos: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    if (datos.isEmpty()) {
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

    LaunchedEffect(datos) {
        val entries = datos.mapIndexed { index, (_, monto) ->
            BarEntry(index.toFloat(), monto)
        }

        val dataSet = BarDataSet(entries, "").apply {
            color = GymGreen.toArgb()
            valueTextColor = Color.Black.toArgb()
            valueTextSize = 12f
            setDrawValues(true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String = "$${"%.2f".format(value)}"
            }
        }

        barChart.apply {
            this.data = BarData(dataSet).apply {
                barWidth = 0.5f
            }

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(datos.map { it.first })
                labelRotationAngle = if (datos.size > 3) -45f else 0f
                textSize = 12f
                granularity = 1f
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
            }

            axisLeft.apply {
                axisMinimum = 0f
                granularity = 100f
                setDrawGridLines(true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String = "$${"%.0f".format(value)}"
                }
            }

            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
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
                .height(200.dp)
        )
    }
}

// Nuevo gráfico para membresías
@Composable
fun BarChartMembresias(
    datos: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    if (datos.isEmpty()) {
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

    // Colores para las barras
    var colors = listOf(
        GymMediumBlue.toArgb(),
        GymGreen.toArgb(),
        GymPurple.toArgb(),
        GymOrange.toArgb(),
        GymPink.toArgb()
    )

    LaunchedEffect(datos) {
        val entries = datos.mapIndexed { index, (_, cantidad) ->
            BarEntry(index.toFloat(), cantidad)
        }

        val dataSet = BarDataSet(entries, "").apply {
            colors = datos.indices.map { index ->
                colors.getOrElse(index % colors.size) { GymMediumBlue.toArgb() }
            }
            valueTextColor = Color.Black.toArgb()
            valueTextSize = 12f
            setDrawValues(true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String = value.toInt().toString()
            }
        }

        barChart.apply {
            this.data = BarData(dataSet).apply {
                barWidth = 0.5f
            }

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(datos.map { it.first })
                labelRotationAngle = if (datos.size > 3) -45f else 0f
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
            description.isEnabled = false
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
                .height(200.dp)
        )
    }
}

// Versión simplificada del gráfico de estados
@Composable
fun BarChartEstadoCantidad(
    datos: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    if (datos.isEmpty()) {
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

    LaunchedEffect(datos) {
        val entries = datos.mapIndexed { index, (_, cantidad) ->
            BarEntry(index.toFloat(), cantidad)
        }

        val dataSet = BarDataSet(entries, "").apply {
            colors = datos.map { (estado, _) ->
                when (estado) {
                    "ACTIVO" -> GymGreen.toArgb()
                    else -> GymBrightRed.toArgb()
                }
            }
            valueTextColor = Color.Black.toArgb()
            valueTextSize = 12f
            setDrawValues(true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String = value.toInt().toString()
            }
        }

        barChart.apply {
            this.data = BarData(dataSet).apply {
                barWidth = 0.5f
            }

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(datos.map { it.first })
                labelRotationAngle = if (datos.size > 3) -45f else 0f
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
            description.isEnabled = false
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
                .height(200.dp)
        )
    }
}
