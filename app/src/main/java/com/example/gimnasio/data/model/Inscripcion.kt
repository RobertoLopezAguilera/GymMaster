package com.example.gimnasio.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inscripciones",
    foreignKeys = [
        ForeignKey(entity = Usuario::class, parentColumns = ["id"], childColumns = ["idUsuario"]),
        ForeignKey(entity = Membresia::class, parentColumns = ["id"], childColumns = ["idMembresia"])
    ],
    indices = [Index("idUsuario"), Index("idMembresia")]
)
data class Inscripcion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idUsuario: Int = 0,
    val idMembresia: Int = 0,
    val fechaPago: String = "",
    val fechaVencimiento: String = "",
    val pagado: Boolean = false
)

