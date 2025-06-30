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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.gimnasio.R
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.time.LocalDate

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

    // Estados para los filtros
    var filtroTipo by remember { mutableStateOf(FilterType.YEAR) }
    var filtroAño by remember { mutableStateOf(LocalDate.now().year.toString()) }
    var filtroMes by remember { mutableStateOf(String.format("%02d", LocalDate.now().monthValue)) }
    var filtroGenero by remember { mutableStateOf<Genero?>(null) }
    var filtroExperiencia by remember { mutableStateOf<Experiencia?>(null) }

    // Calcular el filtro de fecha
    val filtroFecha = if (filtroTipo == FilterType.MONTH) "$filtroAño-$filtroMes" else filtroAño

    // Obtener usuarios y aplicar filtros
    val usuarios by viewModel.getUsuariosPorFiltro(filtroFecha, filtroTipo).collectAsState(initial = emptyList())

    val usuariosFiltrados = remember(usuarios, filtroGenero, filtroExperiencia) {
        usuarios.filter { usuario ->
            (filtroGenero == null || usuario.genero == filtroGenero.toString()) &&
                    (filtroExperiencia == null || usuario.experiencia == filtroExperiencia.toString())
        }
    }

    // Calcular estadísticas de género
    val estadisticasGenero by remember(usuarios) {
        derivedStateOf {
            val total = usuarios.size.toFloat()
            if (total == 0f) {
                mapOf(
                    Genero.MASCULINO to 0f,
                    Genero.FEMENINO to 0f,
                    Genero.OTRO to 0f
                )
            } else {
                mapOf(
                    Genero.MASCULINO to usuarios.count { it.genero == Genero.MASCULINO.toString() } / total * 100,
                    Genero.FEMENINO to usuarios.count { it.genero == Genero.FEMENINO.toString() } / total * 100,
                    Genero.OTRO to usuarios.count { it.genero == Genero.OTRO.toString() } / total * 100
                )
            }
        }
    }

    // Calcular cantidad de usuarios por género
    val cantidadPorGenero by remember(usuarios) {
        derivedStateOf {
            mapOf(
                Genero.MASCULINO to usuarios.count { it.genero == Genero.MASCULINO.toString() }.toFloat(),
                Genero.FEMENINO to usuarios.count { it.genero == Genero.FEMENINO.toString() }.toFloat(),
                Genero.OTRO to usuarios.count { it.genero == Genero.OTRO.toString() }.toFloat()
            )
        }
    }

    // Calcular estadísticas de experiencia
    val estadisticasExperiencia by remember(usuarios) {
        derivedStateOf {
            val total = usuarios.size.toFloat()
            if (total == 0f) {
                mapOf(
                    Experiencia.PRINCIPIANTE to 0f,
                    Experiencia.INTERMEDIO to 0f,
                    Experiencia.AVANZADO to 0f,
                    Experiencia.MIXTO to 0f
                )
            } else {
                mapOf(
                    Experiencia.PRINCIPIANTE to usuarios.count { it.experiencia == Experiencia.PRINCIPIANTE.toString() } / total * 100,
                    Experiencia.INTERMEDIO to usuarios.count { it.experiencia == Experiencia.INTERMEDIO.toString() } / total * 100,
                    Experiencia.AVANZADO to usuarios.count { it.experiencia == Experiencia.AVANZADO.toString() } / total * 100,
                    Experiencia.MIXTO to usuarios.count { it.experiencia == Experiencia.MIXTO.toString() } / total * 100
                )
            }
        }
    }

    // Calcular cantidad de usuarios por experiencia
    val cantidadPorExperiencia by remember(usuarios) {
        derivedStateOf {
            mapOf(
                Experiencia.PRINCIPIANTE to usuarios.count { it.experiencia == Experiencia.PRINCIPIANTE.toString() }.toFloat(),
                Experiencia.INTERMEDIO to usuarios.count { it.experiencia == Experiencia.INTERMEDIO.toString() }.toFloat(),
                Experiencia.AVANZADO to usuarios.count { it.experiencia == Experiencia.AVANZADO.toString() }.toFloat(),
                Experiencia.MIXTO to usuarios.count { it.experiencia == Experiencia.MIXTO.toString() }.toFloat()
            )
        }
    }

    // Preparar datos para el gráfico temporal
    val datosGraficaTemporal by remember(filtroTipo, usuariosFiltrados) {
        derivedStateOf {
            when (filtroTipo) {
                FilterType.MONTH -> {
                    usuariosFiltrados.groupBy { it.fechaInscripcion }
                        .map { (fecha, lista) -> fecha.substring(8) to lista.size.toFloat() }
                        .sortedBy { it.first }
                }
                FilterType.YEAR -> {
                    val porMes = usuariosFiltrados.groupBy { it.fechaInscripcion.substring(5, 7) }
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
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Título con botón de retroceso
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (filtroGenero != null || filtroExperiencia != null) {
                IconButton(
                    onClick = {
                        filtroGenero = null
                        filtroExperiencia = null
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Volver atrás",
                        tint = GymDarkBlue
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = when {
                    filtroGenero != null -> "Estadísticas: ${filtroGenero.toString()}"
                    filtroExperiencia != null -> "Estadísticas: ${filtroExperiencia.toString()}"
                    else -> "Estadísticas de Usuarios"
                },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GymDarkBlue
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filtros
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Filtrar por:", color = GymDarkBlue)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DropdownMenuFiltroTipo(filtroTipo) { filtroTipo = it }
                        Spacer(modifier = Modifier.width(8.dp))
                        DropdownMenuAño(filtroAño, añosDisponibles) { filtroAño = it }
                        if (filtroTipo == FilterType.MONTH) {
                            Spacer(modifier = Modifier.width(8.dp))
                            DropdownMenuMes(filtroMes, mesesMap) { filtroMes = it }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text("Género:", color = GymDarkBlue)
                    FiltroGenero(
                        generoSeleccionado = filtroGenero,
                        onGeneroSeleccionado = { filtroGenero = it }
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Experiencia:", color = GymDarkBlue)
                    FiltroExperiencia(
                        experienciaSeleccionada = filtroExperiencia,
                        onExperienciaSeleccionada = { filtroExperiencia = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta de resumen
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GymWhite),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Total de usuarios
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Usuarios: ", color = GymDarkBlue)
                    Text(
                        usuariosFiltrados.size.toString(),
                        style = MaterialTheme.typography.headlineSmall.copy(color = GymDarkBlue)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (filtroGenero != null || filtroExperiencia != null) {
                        Text(
                            "(${usuarios.size} total)",
                            style = MaterialTheme.typography.bodySmall.copy(color = GymDarkBlue)
                        )
                    }
                }

                // Estadísticas de género (porcentajes)
                if (filtroGenero == null && filtroExperiencia == null && usuarios.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        Text("Distribución por género:", style = MaterialTheme.typography.labelMedium,
                            color = GymDarkBlue)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            estadisticasGenero.forEach { (genero, porcentaje) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = genero.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GymDarkBlue
                                    )
                                    Text(
                                        text = "%.1f%%".format(porcentaje),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = when (genero) {
                                                Genero.MASCULINO -> GymMediumBlue
                                                Genero.FEMENINO -> GymPink
                                                Genero.OTRO -> Purple40
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Estadísticas de experiencia (porcentajes)
                if (filtroGenero == null && filtroExperiencia == null && usuarios.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        Text("Distribución por experiencia:", style = MaterialTheme.typography.labelMedium,
                            color = GymDarkBlue)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            estadisticasExperiencia.forEach { (experiencia, porcentaje) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = experiencia.toString(),
                                        color = GymDarkBlue,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = "%.1f%%".format(porcentaje),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = when (experiencia) {
                                                Experiencia.PRINCIPIANTE -> GymGreen
                                                Experiencia.INTERMEDIO -> GymYellow
                                                Experiencia.AVANZADO -> GymOrange
                                                Experiencia.MIXTO -> GymPurple
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfico de distribución por género (cantidad absoluta)
        if (filtroGenero == null && filtroExperiencia == null && usuarios.isNotEmpty()) {
            Text(
                text = "Distribución por Género",
                color = GymDarkBlue,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            BarChartGeneroCantidad(
                datos = cantidadPorGenero.map { (genero, cantidad) -> genero to cantidad },
                modifier = Modifier.height(200.dp),
                onBarClick = { genero ->
                    filtroGenero = genero
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Gráfico de distribución por experiencia (cantidad absoluta)
        if (filtroGenero == null && filtroExperiencia == null && usuarios.isNotEmpty()) {
            Text(
                text = "Distribución por Experiencia",
                color = GymDarkBlue,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            BarChartExperienciaCantidad(
                datos = cantidadPorExperiencia.map { (experiencia, cantidad) -> experiencia to cantidad },
                modifier = Modifier.height(200.dp),
                onBarClick = { experiencia ->
                    filtroExperiencia = experiencia
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Gráfico de series temporales
        Text(
            text = if (filtroTipo == FilterType.MONTH) "Inscripciones por Día" else "Inscripciones por Mes",
            color = GymDarkBlue,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        BarChartCard(
            data = datosGraficaTemporal,
            barColor = GymMediumBlue,
            modifier = Modifier.height(300.dp),
            onBarClick = { etiqueta ->
                val fechaSeleccionada = when (filtroTipo) {
                    FilterType.MONTH -> LocalDate.parse("$filtroAño-$filtroMes-${etiqueta.padStart(2, '0')}")
                    FilterType.YEAR -> {
                        val mesNum = meses.indexOfFirst { it.startsWith(etiqueta.take(3)) } + 1
                        LocalDate.of(filtroAño.toInt(), mesNum, 1)
                    }
                }
                navController.navigate("inscripciones/${fechaSeleccionada}/mes")
            }
        )
    }
}

@Composable
fun BarChartExperienciaCantidad(
    datos: List<Pair<Experiencia, Float>>,
    modifier: Modifier = Modifier,
    onBarClick: (Experiencia) -> Unit = {}
) {
    if (datos.isEmpty() || datos.all { it.second == 0f }) {
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
            // Colores para cada tipo de experiencia
            colors = datos.map { (experiencia, _) ->
                when (experiencia) {
                    Experiencia.PRINCIPIANTE -> GymGreen.toArgb()
                    Experiencia.INTERMEDIO -> GymYellow.toArgb()
                    Experiencia.AVANZADO -> GymOrange.toArgb()
                    Experiencia.MIXTO -> GymPurple.toArgb()
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
                valueFormatter = IndexAxisValueFormatter(datos.map { it.first.toString() })
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

            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e is BarEntry) {
                        val index = e.x.toInt()
                        if (index in datos.indices) {
                            onBarClick(datos[index].first)
                        }
                    }
                }

                override fun onNothingSelected() {}
            })

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

@Composable
fun BarChartGeneroCantidad(
    datos: List<Pair<Genero, Float>>,
    modifier: Modifier = Modifier,
    onBarClick: (Genero) -> Unit = {}
) {
    if (datos.isEmpty() || datos.all { it.second == 0f }) {
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
            // Asignar colores específicos por género
            colors = datos.map { (genero, _) ->
                when (genero) {
                    Genero.MASCULINO -> GymMediumBlue.toArgb()
                    Genero.FEMENINO -> GymPink.toArgb()
                    Genero.OTRO -> Purple40.toArgb()
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
                valueFormatter = IndexAxisValueFormatter(datos.map { it.first.toString() })
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

            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e is BarEntry) {
                        val index = e.x.toInt()
                        if (index in datos.indices) {
                            onBarClick(datos[index].first)
                        }
                    }
                }

                override fun onNothingSelected() {}
            })

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

@Composable
fun BarChartCard(
    data: List<Pair<String, Float>>,
    barColor: Color,
    modifier: Modifier = Modifier,
    showValuesAsPercent: Boolean = false,
    onBarClick: (String) -> Unit = {}
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

    LaunchedEffect(data) {
        val entries = data.mapIndexed { index, (_, value) ->
            BarEntry(index.toFloat(), value)
        }

        val dataSet = BarDataSet(entries, "").apply {
            color = barColor.toArgb()
            valueTextColor = Color.Black.toArgb()
            valueTextSize = 12f
            setDrawValues(true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (showValuesAsPercent) "%.1f%%".format(value) else value.toInt().toString()
                }
            }
        }

        barChart.apply {
            this.data = BarData(dataSet).apply {
                barWidth = 0.5f
            }

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(data.map { it.first })
                labelRotationAngle = if (data.size > 6) -45f else 0f
                textSize = 12f
                granularity = 1f
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
            }

            axisLeft.apply {
                axisMinimum = 0f
                granularity = if (showValuesAsPercent) 20f else 1f
                setDrawGridLines(true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return if (showValuesAsPercent) "%.0f%%".format(value) else value.toInt().toString()
                    }
                }
            }

            axisRight.isEnabled = false
            legend.isEnabled = false
            description.isEnabled = false

            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e is BarEntry) {
                        val index = e.x.toInt()
                        if (index in data.indices) {
                            onBarClick(data[index].first)
                        }
                    }
                }

                override fun onNothingSelected() {}
            })

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

@Composable
fun FiltroExperiencia(
    experienciaSeleccionada: Experiencia?,
    onExperienciaSeleccionada: (Experiencia?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = GymWhite,
                contentColor = GymDarkBlue
            )
        ) {
            Text(
                text = experienciaSeleccionada?.toString() ?: "Todos",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(GymWhite)
        ) {
            DropdownMenuItem(
                text = { Text("Todos") },
                onClick = {
                    onExperienciaSeleccionada(null)
                    expanded = false
                }
            )
            Experiencia.values().forEach { experiencia ->
                DropdownMenuItem(
                    text = { Text(experiencia.toString()) },
                    onClick = {
                        onExperienciaSeleccionada(experiencia)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun FiltroGenero(
    generoSeleccionado: Genero?,
    onGeneroSeleccionado: (Genero?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = GymWhite,
                contentColor = GymDarkBlue
            )
        ) {
            Text(
                text = generoSeleccionado?.toString() ?: "Todos",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(GymWhite)
        ) {
            DropdownMenuItem(
                text = { Text("Todos") },
                onClick = {
                    onGeneroSeleccionado(null)
                    expanded = false
                }
            )
            Genero.values().forEach { genero ->
                DropdownMenuItem(
                    text = { Text(genero.toString()) },
                    onClick = {
                        onGeneroSeleccionado(genero)
                        expanded = false
                    }
                )
            }
        }
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
            color = GymDarkBlue,
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
            color = GymDarkBlue,
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
            color = GymDarkBlue,
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

enum class Genero {
    MASCULINO,
    FEMENINO,
    OTRO;

    override fun toString(): String {
        return when (this) {
            MASCULINO -> "Masculino"
            FEMENINO -> "Femenino"
            OTRO -> "Otro"
        }
    }
}

enum class Experiencia {
    PRINCIPIANTE,
    INTERMEDIO,
    AVANZADO,
    MIXTO;

    override fun toString(): String {
        return when (this) {
            PRINCIPIANTE -> "Principiante"
            INTERMEDIO -> "Intermedio"
            AVANZADO -> "Avanzado"
            MIXTO -> "Mixto"
        }
    }
}