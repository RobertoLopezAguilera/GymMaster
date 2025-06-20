package com.example.gimnasio.data.dao

import androidx.room.*
import com.example.gimnasio.data.model.Inscripcion
import kotlinx.coroutines.flow.Flow

@Dao
interface InscripcionDao {
    @Query("SELECT * FROM inscripciones")
    fun getAll(): Flow<List<Inscripcion>>

    @Query("SELECT * FROM inscripciones WHERE idUsuario = :usuarioId")
    fun getByUsuario(usuarioId: Int): Flow<List<Inscripcion>>

    @Query("SELECT * FROM inscripciones WHERE idUsuario = :usuarioId ORDER BY id DESC LIMIT 1")
    fun getByUsuarioId(usuarioId: Int): Flow<Inscripcion?>

//    @Query("SELECT * FROM inscripciones WHERE fechaPago = :fecha")
//    fun obtenerInscripcionesPorFechaPago(fecha: String): Flow<List<Inscripcion>>

    @Query("SELECT * FROM inscripciones WHERE fechaPago = :fecha AND id IN ( SELECT MAX(id) " +
            "FROM inscripciones WHERE fechaPago = :fecha GROUP BY idUsuario)")
    fun obtenerInscripcionesPorFechaPago(fecha: String): Flow<List<Inscripcion>>

//    @Query("SELECT * FROM inscripciones WHERE fechaVencimiento = :fecha")
//    fun obtenerInscripcionesPorFechaVencimiento(fecha: String): Flow<List<Inscripcion>>

    @Query("SELECT * FROM inscripciones WHERE fechaVencimiento = :fecha AND id IN (SELECT MAX(id) " +
            "FROM inscripciones WHERE fechaVencimiento = :fecha GROUP BY idUsuario)")
    fun obtenerInscripcionesPorFechaVencimiento(fecha: String): Flow<List<Inscripcion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(inscripcion: Inscripcion)

    @Update
    suspend fun update(inscripcion: Inscripcion)

    @Delete
    suspend fun delete(inscripcion: Inscripcion)

    @Query("DELETE FROM inscripciones WHERE idUsuario = :usuarioId")
    suspend fun eliminarPorUsuario(usuarioId: Int)
}
