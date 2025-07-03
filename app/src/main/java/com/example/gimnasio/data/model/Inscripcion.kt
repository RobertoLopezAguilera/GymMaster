package com.example.gimnasio.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "inscripciones",
    foreignKeys = [
        ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["idUsuario"]),
        ForeignKey(entity = Membresia::class, parentColumns = ["id"], childColumns = ["idMembresia"])
    ],
    indices = [Index("idUsuario"), Index("idMembresia")]
)
data class Inscripcion(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val idUsuario: String = "",
    val idMembresia: String = "",
    val fechaPago: String = "",
    val fechaVencimiento: String = "",
    val pagado: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)

