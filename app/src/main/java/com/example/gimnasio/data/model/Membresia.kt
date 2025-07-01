package com.example.gimnasio.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "membresias")
data class Membresia(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: String = "",
    val costo: Double = 0.0,
    val duracionDias: Int = 0
)

