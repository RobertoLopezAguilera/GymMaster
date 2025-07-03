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
import java.util.UUID

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

                // Crear usuarios con ID manual
                val usuarios = listOf(
                    Usuario(id = UUID.randomUUID().toString(), nombre = "Carlos Ramírez", genero = "Masculino", edad = 28, peso = 75.0, experiencia = "Intermedio", fechaInscripcion = hoy.toString()),
                    Usuario(id = UUID.randomUUID().toString(), nombre = "Ana López", genero = "Femenino", edad = 24, peso = 60.5, experiencia = "Principiante", fechaInscripcion = hoy.toString()),
                    Usuario(id = UUID.randomUUID().toString(), nombre = "Luis Gómez", genero = "Masculino", edad = 32, peso = 82.3, experiencia = "Avanzado", fechaInscripcion = hoy.toString()),
                    Usuario(id = UUID.randomUUID().toString(), nombre = "María García", genero = "Femenino", edad = 29, peso = 58.0, experiencia = "Mixto", fechaInscripcion = hoy.toString()),
                    Usuario(id = UUID.randomUUID().toString(), nombre = "Jorge Martínez", genero = "Masculino", edad = 35, peso = 90.0, experiencia = "Avanzado", fechaInscripcion = hoy.toString()),
                    Usuario(id = UUID.randomUUID().toString(), nombre = "Sofía Pérez", genero = "Femenino", edad = 22, peso = 55.4, experiencia = "Principiante", fechaInscripcion = hoy.toString()),
                    Usuario(id = UUID.randomUUID().toString(), nombre = "Diego Torres", genero = "Masculino", edad = 27, peso = 78.8, experiencia = "Intermedio", fechaInscripcion = hoy.toString()),
                    Usuario(id = UUID.randomUUID().toString(), nombre = "Lucía Fernández", genero = "Femenino", edad = 30, peso = 62.1, experiencia = "Mixto", fechaInscripcion = hoy.toString())
                )
                usuarios.forEach { usuarioDao.insert(it) }

                // Crear membresías con ID manual
                val membresias = listOf(
                    Membresia(id = UUID.randomUUID().toString(), tipo = "Mensual", costo = 400.0, duracionDias = 30),
                    Membresia(id = UUID.randomUUID().toString(), tipo = "Semanal", costo = 120.0, duracionDias = 7),
                    Membresia(id = UUID.randomUUID().toString(), tipo = "Prueba", costo = 50.0, duracionDias = 3),
                    Membresia(id = UUID.randomUUID().toString(), tipo = "Trimestral", costo = 1000.0, duracionDias = 90)
                )
                membresias.forEach { membresiaDao.insert(it) }

                // Crear un mapa para acceder fácil a los IDs
                val usuariosMap = usuarios.associateBy { it.nombre }
                val membresiasMap = membresias.associateBy { it.tipo }

                // Crear inscripciones con ID manual y referencias por ID string
                val inscripciones = listOf(
                    // Carlos Ramírez - Mensual
                    Inscripcion(id = UUID.randomUUID().toString(), idUsuario = usuariosMap["Carlos Ramírez"]!!.id, idMembresia = membresiasMap["Mensual"]!!.id, fechaPago = "2025-04-01", fechaVencimiento = "2025-05-01", pagado = true),
                    Inscripcion(id = UUID.randomUUID().toString(), idUsuario = usuariosMap["Carlos Ramírez"]!!.id, idMembresia = membresiasMap["Mensual"]!!.id, fechaPago = "2025-05-01", fechaVencimiento = "2025-06-01", pagado = true),
                    Inscripcion(id = UUID.randomUUID().toString(), idUsuario = usuariosMap["Carlos Ramírez"]!!.id, idMembresia = membresiasMap["Mensual"]!!.id, fechaPago = "2025-06-01", fechaVencimiento = "2025-07-01", pagado = true),

                    // Ana López - Semanal
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Ana López"]!!.id, membresiasMap["Semanal"]!!.id, "2025-06-01", "2025-06-08", true),
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Ana López"]!!.id, membresiasMap["Semanal"]!!.id, "2025-06-08", "2025-06-15", true),
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Ana López"]!!.id, membresiasMap["Semanal"]!!.id, "2025-06-15", "2025-06-22", true),

                    // Luis Gómez - Trimestral
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Luis Gómez"]!!.id, membresiasMap["Trimestral"]!!.id, "2025-01-10", "2025-04-10", true),
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Luis Gómez"]!!.id, membresiasMap["Trimestral"]!!.id, "2025-04-10", "2025-07-10", true),

                    // María García - Prueba + Mensual
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["María García"]!!.id, membresiasMap["Prueba"]!!.id, "2025-05-10", "2025-05-13", true),
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["María García"]!!.id, membresiasMap["Mensual"]!!.id, "2025-05-13", "2025-06-13", true),

                    // Jorge Martínez
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Jorge Martínez"]!!.id, membresiasMap["Mensual"]!!.id, "2025-03-20", "2025-04-20", true),
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Jorge Martínez"]!!.id, membresiasMap["Mensual"]!!.id, "2025-04-20", "2025-05-20", true),

                    // Sofía Pérez
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Sofía Pérez"]!!.id, membresiasMap["Semanal"]!!.id, "2025-06-01", "2025-06-08", true),
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Sofía Pérez"]!!.id, membresiasMap["Semanal"]!!.id, "2025-06-08", "2025-06-15", true),
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Sofía Pérez"]!!.id, membresiasMap["Semanal"]!!.id, "2025-06-15", "2025-06-22", false),

                    // Diego Torres
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Diego Torres"]!!.id, membresiasMap["Mensual"]!!.id, "2025-02-10", "2025-03-10", true),
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Diego Torres"]!!.id, membresiasMap["Mensual"]!!.id, "2025-03-10", "2025-04-10", true),
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Diego Torres"]!!.id, membresiasMap["Mensual"]!!.id, "2025-04-10", "2025-05-10", true),
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Diego Torres"]!!.id, membresiasMap["Mensual"]!!.id, "2025-05-10", "2025-06-10", true),

                    // Lucía Fernández
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Lucía Fernández"]!!.id, membresiasMap["Prueba"]!!.id, "2025-06-01", "2025-06-04", true),
                    Inscripcion(UUID.randomUUID().toString(), usuariosMap["Lucía Fernández"]!!.id, membresiasMap["Mensual"]!!.id, "2025-06-04", "2025-07-04", true)
                )

                inscripciones.forEach { inscripcionDao.insert(it) }
            }
        }
    }

}
