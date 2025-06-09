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

    init {
        insertarMembresiasDePrueba()
    }

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

    fun eliminarMembresia(membresiaID: Int) {
        viewModelScope.launch {
            val membresia = membresiaDao.getById(membresiaID).firstOrNull()
            membresia?.let {
                membresiaDao.delete(it)
            }
        }
    }

    fun eliminarMembresiaPorId(id: Int) {
        viewModelScope.launch {
            val membresia = membresiaDao.getById(id).firstOrNull()
            membresia?.let {
                membresiaDao.delete(it)
            }
        }
    }

    fun obtenerMembresiaPorId(id: Int): Flow<Membresia?> {
        return membresiaDao.getById(id)
    }

    fun actualizarMembresia(membresia: Membresia) {
        viewModelScope.launch {
            membresiaDao.update(membresia)
        }
    }


    private fun insertarMembresiasDePrueba() {
        viewModelScope.launch {
            if (membresiaDao.getCount() == 0) {
                val membresias = listOf(
                    Membresia(tipo = "Mensual", costo = 400.0, duracionDias = 30),
                    Membresia(tipo = "Semanal", costo = 120.0, duracionDias = 7),
                    Membresia(tipo = "Prueba", costo = 50.0, duracionDias = 3),
                    Membresia(tipo = "Trimestral", costo = 1000.0, duracionDias = 90)
                )
                membresias.forEach { membresiaDao.insert(it) }
            }
        }
    }
}
