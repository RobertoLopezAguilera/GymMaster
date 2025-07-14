package com.example.gimnasio.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey override val id: String = UUID.randomUUID().toString(),
    val nombre: String = "",
    val genero: String = "",
    val edad: Int = 0,
    val peso: Double = 0.0,
    val experiencia: String = "",
    val fechaInscripcion: String = "",
    override val lastUpdated: Long = System.currentTimeMillis()
) : FirestoreSyncable


