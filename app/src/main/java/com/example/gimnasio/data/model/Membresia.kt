package com.example.gimnasio.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "membresias")
data class Membresia(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val tipo: String = "",
    val costo: Double = 0.0,
    val duracionDias: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

