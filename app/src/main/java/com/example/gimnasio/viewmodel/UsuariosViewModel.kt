package com.example.gimnasio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gimnasio.data.AppDatabase
import com.example.gimnasio.data.dao.UsuarioDao
import com.example.gimnasio.data.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()

    init {
        insertarUsuariosDePrueba()
    }

    // Flow observable para la lista de usuarios
    val usuarios: StateFlow<List<Usuario>> = usuarioDao.getAll()
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
                val clientesPrueba = listOf(
                    Usuario(nombre = "Carlos Ramírez", genero = "Masculino", edad = 28, peso = 75.0, experiencia = "Intermedio"),
                    Usuario(nombre = "Ana López", genero = "Femenino", edad = 24, peso = 60.5, experiencia = "Principiante"),
                    Usuario(nombre = "Luis Gómez", genero = "Masculino", edad = 32, peso = 82.3, experiencia = "Avanzado"),
                    Usuario(nombre = "María García", genero = "Femenino", edad = 29, peso = 58.0, experiencia = "Mixto"),
                    Usuario(nombre = "Jorge Martínez", genero = "Masculino", edad = 35, peso = 90.0, experiencia = "Avanzado"),
                    Usuario(nombre = "Sofía Pérez", genero = "Femenino", edad = 22, peso = 55.4, experiencia = "Principiante"),
                    Usuario(nombre = "Diego Torres", genero = "Masculino", edad = 27, peso = 78.8, experiencia = "Intermedio"),
                    Usuario(nombre = "Lucía Fernández", genero = "Femenino", edad = 30, peso = 62.1, experiencia = "Mixto")
                )
                clientesPrueba.forEach { usuarioDao.insert(it) }
            }
        }
    }
}
