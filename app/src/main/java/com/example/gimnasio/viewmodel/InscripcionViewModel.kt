package com.example.gimnasio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gimnasio.data.AppDatabase
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.data.model.Membresia
import com.example.gimnasio.data.model.Usuario
import com.example.gimnasio.ui.FilterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import kotlinx.coroutines.flow.flowOf
import java.time.format.DateTimeFormatter


class InscripcionViewModel(application: Application) : AndroidViewModel(application) {

    private val inscripcionDao = AppDatabase.getDatabase(application).inscripcionDao()
    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()
    private val membresiaDao = AppDatabase.getDatabase(application).membresiaDao()

    fun insertarInscripcion(inscripcion: Inscripcion) {
        viewModelScope.launch {
            inscripcionDao.insert(inscripcion)
        }
    }

    fun actualizarIncripcion(inscripcion: Inscripcion){
        viewModelScope.launch {
            inscripcionDao.update(inscripcion)
        }
    }
    fun getInscripcionesPorFiltro(filtro: String, tipo: FilterType): Flow<List<Inscripcion>> {
        return when (tipo) {
            FilterType.MONTH -> inscripcionDao.getInscripcionesPorMes(filtro)
            FilterType.YEAR -> inscripcionDao.getInscripcionesPorAño(filtro)
        }
    }

    // Función para parsear fechas
    fun parseFecha(fecha: String): LocalDate {
        return LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE)
    }

    val membresias: Flow<List<Membresia>> = membresiaDao.getAll()

    fun obtenerInscripcionPorUsuario(usuarioId: String): Flow<Inscripcion?> {
        return inscripcionDao.getByUsuarioId(usuarioId)
    }

    fun obtenerrUsuarioPorInscripcion(usuarioId: String): Flow<Usuario?> {
        return usuarioDao.getUsuarioPorId(usuarioId)
    }

    fun getAllUsuarios():Flow<List<Inscripcion>>{
        return inscripcionDao.getAll()
    }

    fun getByUsuario(usuarioId: String): Flow<List<Inscripcion>> {
        return inscripcionDao.getByUsuario(usuarioId)
    }

    fun eliminarInscripcion(inscripcion: Inscripcion) {
        viewModelScope.launch {
            inscripcionDao.delete(inscripcion)
        }
    }

    fun inscripcionesPorDia(): Flow<Map<LocalDate, Int>> {
        return inscripcionDao.getAll()
            .map { lista ->
                lista.groupingBy {
                    val fechaLong = stringToMillis(it.fechaVencimiento)
                    Instant.ofEpochMilli(fechaLong).atZone(ZoneId.systemDefault()).toLocalDate()
                }.eachCount()
            }
    }

    fun stringToMillis(dateString: String, pattern: String = "yyyy-MM-dd"): Long {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.parse(dateString)?.time ?: 0L
    }

    fun inscripcionesPor(fechaTipo: (Inscripcion) -> String): Flow<Map<LocalDate, Int>> {
        return inscripcionDao.getAll()
            .map { lista ->
                lista.groupingBy {
                    val fechaLong = stringToMillis(fechaTipo(it))
                    Instant.ofEpochMilli(fechaLong).atZone(ZoneId.systemDefault()).toLocalDate()
                }.eachCount()
            }
    }

    fun inscripcionesPorTipo(tipo: String): Flow<Map<LocalDate, Int>> {
        return when (tipo) {
            "pago" -> {
                inscripcionDao.getAll()
                    .map { it.map { ins -> ins.fechaPago } }
                    .map { fechas -> fechas.distinct() }
                    .flatMapLatest { fechas ->
                        if (fechas.isEmpty()) flowOf(emptyMap())
                        else {
                            combine(
                                fechas.map { fecha ->
                                    inscripcionDao.obtenerInscripcionesPorFechaPago(fecha).map { lista ->
                                        LocalDate.parse(fecha) to lista.size
                                    }
                                }
                            ) { resultados -> resultados.toMap() }
                        }
                    }
            }

            "vencimiento" -> {
                inscripcionDao.getAll()
                    .map { it.map { ins -> ins.fechaVencimiento } }
                    .map { fechas -> fechas.distinct() }
                    .flatMapLatest { fechas ->
                        if (fechas.isEmpty()) flowOf(emptyMap())
                        else {
                            combine(
                                fechas.map { fecha ->
                                    inscripcionDao.obtenerInscripcionesPorFechaVencimiento(fecha).map { lista ->
                                        LocalDate.parse(fecha) to lista.size
                                    }
                                }
                            ) { resultados -> resultados.toMap() }
                        }
                    }
            }

            else -> {
                usuarioDao.getUsuarios()
                    .map { usuarios ->
                        usuarios.groupingBy {
                            val fechaLong = stringToMillis(it.fechaInscripcion ?: "2000-01-01")
                            Instant.ofEpochMilli(fechaLong).atZone(ZoneId.systemDefault()).toLocalDate()
                        }.eachCount()
                    }
            }
        }
    }

    fun getUsuariosPorFiltro(mesAnio: String, filterType: FilterType): Flow<List<Usuario>> {
        return when (filterType) {
            FilterType.YEAR -> usuarioDao.getUsuariosPorAño(mesAnio)
            FilterType.MONTH -> usuarioDao.getUsuariosPorMes(mesAnio)
        }
    }

    // Métodos específicos para cada caso
    fun getUsuariosPorMes(mesAnio: String): Flow<List<Usuario>> {
        return usuarioDao.getUsuariosPorMes(mesAnio)
    }

    fun getUsuariosPorMesYGenero(mesAnio: String, genero: String): Flow<List<Usuario>> {
        return usuarioDao.getUsuariosPorMesGenero(mesAnio, genero)
    }

    fun getUsuariosPorMesYExperiencia(mesAnio: String, experiencia: String): Flow<List<Usuario>> {
        return usuarioDao.getUsuariosPorMesExperiencia(mesAnio, experiencia.toString())
    }

    fun obtenerInscripcionesPorFechaPago(fecha: LocalDate): Flow<List<Inscripcion>> {
        return inscripcionDao.obtenerInscripcionesPorFechaPago(fecha.toString())
    }

    fun obtenerUltimaInscripcion(usuarioId: String): Flow<Inscripcion?> {
        return inscripcionDao.getByUsuarioId(usuarioId)
    }

    fun obtenerInscripcionesPorFechaVencimiento(fecha: LocalDate): Flow<List<Inscripcion>> {
        return inscripcionDao.obtenerInscripcionesPorFechaVencimiento(fecha.toString())
    }
}