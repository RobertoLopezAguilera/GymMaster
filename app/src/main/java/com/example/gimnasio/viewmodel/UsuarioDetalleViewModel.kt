package com.example.gimnasio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gimnasio.data.AppDatabase
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.data.model.Membresia
import com.example.gimnasio.data.model.Usuario
import com.example.gimnasio.ui.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioDetalleViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val usuarioDao = db.usuarioDao()
    private val inscripcionDao = db.inscripcionDao()
    private val membresiaDao = db.membresiaDao()

    // Funciones existentes
    fun getUsuario(usuarioId: Int): Flow<Usuario?> = usuarioDao.getUsuarioPorId(usuarioId)
    fun getInscripcion(usuarioId: Int): Flow<Inscripcion?> = inscripcionDao.getByUsuarioId(usuarioId)
    fun getMembresia(membresiaId: Int): Flow<Membresia?> = membresiaDao.getById(membresiaId)

    // En tu ViewModel
    val allUsuarios: Flow<Map<Int, Usuario>> = usuarioDao.getUsuariosFlow().map { usuarios ->
        usuarios.associateBy { it.id }
    }

    fun insertarInscripcion(inscripcion: Inscripcion) {
        viewModelScope.launch {
            inscripcionDao.insert(inscripcion)
        }
    }

    fun eliminarUsuarioConTodo(usuarioId: Int, onFinish: () -> Unit) {
        viewModelScope.launch {
            inscripcionDao.eliminarPorUsuario(usuarioId)
            usuarioDao.eliminarPorId(usuarioId)
            onFinish()
        }
    }

    fun actualizarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            usuarioDao.update(usuario)
        }
    }

    // Nuevas funciones para estadísticas

    fun getAllUsuarios2():Flow<List<Inscripcion>>{
        return inscripcionDao.getAll()
    }

    suspend fun getUsuarioSinFlow(usuarioId: Int): Usuario? {
        return withContext(Dispatchers.IO) {
            usuarioDao.getByIdDirecto(usuarioId)
        }
    }


    fun getUsuariosPorMes(mes: String): Flow<List<Usuario>> {
        return usuarioDao.getUsuariosPorMes(mes)
    }

    // Usuarios por AÑO (ej: "2025")
    fun getUsuariosPorAño(año: String): Flow<List<Usuario>> {
        return usuarioDao.getUsuariosPorAño(año)
    }


    fun getUsuariosPorFiltro(filtro: String, filterType: FilterType): Flow<List<Usuario>> {
        return when (filterType) {
            FilterType.YEAR -> usuarioDao.getUsuariosPorAño(filtro)
            FilterType.MONTH -> usuarioDao.getUsuariosPorMes(filtro)
        }
    }
}