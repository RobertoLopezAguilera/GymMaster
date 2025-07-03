package com.example.gimnasio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gimnasio.data.AppDatabase
import com.example.gimnasio.data.model.Membresia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull


class MembresiasViewModel(application: Application) : AndroidViewModel(application) {

    private val membresiaDao = AppDatabase.getDatabase(application).membresiaDao()

    // Flow observable de membresías
    val membresias: StateFlow<List<Membresia>> = membresiaDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Insertar membresía
    fun insertarMembresia(membresia: Membresia) {
        viewModelScope.launch {
            membresiaDao.insert(membresia)
        }
    }

    fun eliminarMembresia(membresiaID: String) {
        viewModelScope.launch {
            val membresia = membresiaDao.getById(membresiaID).firstOrNull()
            membresia?.let {
                membresiaDao.delete(it)
            }
        }
    }

    fun eliminarMembresiaPorId(id: String) {
        viewModelScope.launch {
            val membresia = membresiaDao.getById(id).firstOrNull()
            membresia?.let {
                membresiaDao.delete(it)
            }
        }
    }

    fun obtenerMembresiaPorId(id: String): Flow<Membresia?> {
        return membresiaDao.getById(id)
    }

    fun actualizarMembresia(membresia: Membresia) {
        viewModelScope.launch {
            membresiaDao.update(membresia)
        }
    }
}
