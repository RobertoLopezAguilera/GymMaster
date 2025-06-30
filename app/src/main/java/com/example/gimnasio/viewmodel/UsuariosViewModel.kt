package com.example.gimnasio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gimnasio.data.AppDatabase
import com.example.gimnasio.data.dao.UsuarioDao
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.data.model.Membresia
import com.example.gimnasio.data.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()
    private val membresiaDao = AppDatabase.getDatabase(application).membresiaDao()
    private val inscripcionDao = AppDatabase.getDatabase(application).inscripcionDao()

    init {
        insertarUsuariosDePrueba()
    }

    // Flow observable para la lista de usuarios
    val usuarios: StateFlow<List<Usuario>> = usuarioDao.getUsuarios()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
            )

    // Insertar nuevo usuario
    fun insertarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            usuarioDao.insert(usuario)
        }
    }

    // Eliminar usuario
    fun eliminarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            usuarioDao.delete(usuario)
        }
    }

    // Actualizar usuario
    fun actualizarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            usuarioDao.update(usuario)
        }
    }

    private fun insertarUsuariosDePrueba() {
        viewModelScope.launch {
            if (usuarioDao.getCount() == 0) {
                val hoy = LocalDate.now()
                val clientesPrueba = listOf(
                    Usuario(nombre = "Carlos Ramírez", genero = "Masculino", edad = 28, peso = 75.0, experiencia = "Intermedio", fechaInscripcion = hoy.toString()),
                    Usuario(nombre = "Ana López", genero = "Femenino", edad = 24, peso = 60.5, experiencia = "Principiante", fechaInscripcion = hoy.toString()),
                    Usuario(nombre = "Luis Gómez", genero = "Masculino", edad = 32, peso = 82.3, experiencia = "Avanzado", fechaInscripcion = hoy.toString()),
                    Usuario(nombre = "María García", genero = "Femenino", edad = 29, peso = 58.0, experiencia = "Mixto", fechaInscripcion = hoy.toString()),
                    Usuario(nombre = "Jorge Martínez", genero = "Masculino", edad = 35, peso = 90.0, experiencia = "Avanzado", fechaInscripcion = hoy.toString()),
                    Usuario(nombre = "Sofía Pérez", genero = "Femenino", edad = 22, peso = 55.4, experiencia = "Principiante", fechaInscripcion = hoy.toString()),
                    Usuario(nombre = "Diego Torres", genero = "Masculino", edad = 27, peso = 78.8, experiencia = "Intermedio", fechaInscripcion = hoy.toString()),
                    Usuario(nombre = "Lucía Fernández", genero = "Femenino", edad = 30, peso = 62.1, experiencia = "Mixto", fechaInscripcion = hoy.toString())
                )
                clientesPrueba.forEach { usuarioDao.insert(it) }

                val membresias = listOf(
                    Membresia(tipo = "Mensual", costo = 400.0, duracionDias = 30),
                    Membresia(tipo = "Semanal", costo = 120.0, duracionDias = 7),
                    Membresia(tipo = "Prueba", costo = 50.0, duracionDias = 3),
                    Membresia(tipo = "Trimestral", costo = 1000.0, duracionDias = 90)
                )
                membresias.forEach { membresiaDao.insert(it) }
                val inscripciones = listOf(
                    Inscripcion(1, 1, 1, "2025-04-01", "2025-05-01", true),
                    Inscripcion(2, 1, 1, "2025-05-01", "2025-06-01", true),
                    Inscripcion(3, 1, 1, "2025-06-01", "2025-07-01", true),

                    // Usuario 2 - Ana López - Semanal
                    Inscripcion(4, 2, 2, "2025-06-01", "2025-06-08", true),
                    Inscripcion(5, 2, 2, "2025-06-08", "2025-06-15", true),
                    Inscripcion(6, 2, 2, "2025-06-15", "2025-06-22", true),

                    // Usuario 3 - Luis Gómez - Trimestral
                    Inscripcion(7, 3, 4, "2025-01-10", "2025-04-10", true),
                    Inscripcion(8, 3, 4, "2025-04-10", "2025-07-10", true),

                    // Usuario 4 - María García - Prueba + Mensual
                    Inscripcion(9, 4, 3, "2025-05-10", "2025-05-13", true),
                    Inscripcion(10, 4, 1, "2025-05-13", "2025-06-13", true),

                    // Usuario 5 - Jorge Martínez - Mensual
                    Inscripcion(11, 5, 1, "2025-03-20", "2025-04-20", true),
                    Inscripcion(12, 5, 1, "2025-04-20", "2025-05-20", true),

                    // Usuario 6 - Sofía Pérez - Semanal (última sin pagar)
                    Inscripcion(13, 6, 2, "2025-06-01", "2025-06-08", true),
                    Inscripcion(14, 6, 2, "2025-06-08", "2025-06-15", true),
                    Inscripcion(15, 6, 2, "2025-06-15", "2025-06-22", false),

                    // Usuario 7 - Diego Torres - Mensual
                    Inscripcion(16, 7, 1, "2025-02-10", "2025-03-10", true),
                    Inscripcion(17, 7, 1, "2025-03-10", "2025-04-10", true),
                    Inscripcion(18, 7, 1, "2025-04-10", "2025-05-10", true),
                    Inscripcion(19, 7, 1, "2025-05-10", "2025-06-10", true),

                    // Usuario 8 - Lucía Fernández - Prueba, luego mensual
                    Inscripcion(20, 8, 3, "2025-06-01", "2025-06-04", true),
                    Inscripcion(21, 8, 1, "2025-06-04", "2025-07-04", true)
                )
                inscripciones.forEach { inscripcionDao.insert(it) }
            }
        }
    }
}
