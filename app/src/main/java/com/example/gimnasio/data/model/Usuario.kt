package com.example.gimnasio.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val genero: String,
    val edad: Int,
    val peso: Double,
    val experiencia: String // Principiante, Intermedio, Avanzado, Mixto
)
