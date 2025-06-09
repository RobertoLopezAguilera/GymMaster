package com.example.gimnasio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gimnasio.data.AppDatabase
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.data.model.Membresia
import com.example.gimnasio.data.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UsuarioDetalleViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val usuarioDao = db.usuarioDao()
    private val inscripcionDao = db.inscripcionDao()
    private val membresiaDao = db.membresiaDao()

    fun getUsuario(usuarioId: Int): Flow<Usuario?> = usuarioDao.getUsuarioPorId(usuarioId)

    fun getInscripcion(usuarioId: Int): Flow<Inscripcion?> = inscripcionDao.getByUsuarioId(usuarioId)

    fun getMembresia(membresiaId: Int): Flow<Membresia?> = membresiaDao.getById(membresiaId)

    fun insertarInscripcion(inscripcion: Inscripcion) {
        viewModelScope.launch {
            inscripcionDao.insert(inscripcion)
        }
    }

    fun eliminarUsuarioConTodo(usuarioId: Int, onFinish: () -> Unit) {
        viewModelScope.launch {
            // Elimina la inscripción del usuario si existe
            inscripcionDao.eliminarPorUsuario(usuarioId)

            // Luego elimina al usuario
            usuarioDao.eliminarPorId(usuarioId)

            onFinish()
        }
    }

    fun actualizarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            usuarioDao.update(usuario)
        }
    }


}
