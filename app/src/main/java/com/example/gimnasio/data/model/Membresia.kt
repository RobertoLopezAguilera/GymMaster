package com.example.gimnasio.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "membresias")
data class Membresia(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: String,     // Mensual, Semanal, Prueba, etc.
    val costo: Double,
    val duracionDias: Int
)
