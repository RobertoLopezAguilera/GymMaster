package com.robertolopezaguilera.gimnasio.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "membresias")
data class Membresia(
    @PrimaryKey override val id: String = UUID.randomUUID().toString(),
    val tipo: String = "",
    val costo: Double = 0.0,
    val duracionDias: Int = 0,
    override val lastUpdated: Long = System.currentTimeMillis()
) : FirestoreSyncable


