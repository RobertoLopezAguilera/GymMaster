package com.example.gimnasio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gimnasio.data.AppDatabase
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.data.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

class InscripcionViewModel(application: Application) : AndroidViewModel(application) {

    private val inscripcionDao = AppDatabase.getDatabase(application).inscripcionDao()
    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()

    fun insertarInscripcion(inscripcion: Inscripcion) {
        viewModelScope.launch {
            inscripcionDao.insert(inscripcion)
        }
    }

    fun obtenerInscripcionPorUsuario(usuarioId: Int): Flow<Inscripcion?> {
        return inscripcionDao.getByUsuarioId(usuarioId)
    }

    fun obtenerrUsuarioPorInscripcion(usuarioId: Int): Flow<Usuario?> {
        return usuarioDao.getUsuarioPorId(usuarioId)
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

    fun obtenerInscripcionesPorFecha(
        fecha: LocalDate,
        fechaTipo: (Inscripcion) -> String
    ): Flow<List<Inscripcion>> {
        return inscripcionDao.getAll()
            .map { lista ->
                lista.filter {
                    val dateLong = stringToMillis(fechaTipo(it))
                    val inscDate = Instant.ofEpochMilli(dateLong).atZone(ZoneId.systemDefault()).toLocalDate()
                    inscDate == fecha
                }
            }
    }

    fun obtenerInscripcionesPorFechaPago(fecha: LocalDate): Flow<List<Inscripcion>> {
        return inscripcionDao.obtenerInscripcionesPorFechaPago(fecha.toString())
    }

    fun obtenerInscripcionesPorFechaInscripcion(fecha: LocalDate): Flow<List<Usuario>> {
        return usuarioDao.obtenerInscripcionesPorFechaInscripcion(fecha.toString())
    }
    fun obtenerInscripcionesPorFechaVencimiento(fecha: LocalDate): Flow<List<Inscripcion>> {
        return inscripcionDao.obtenerInscripcionesPorFechaVencimiento(fecha.toString())
    }



}