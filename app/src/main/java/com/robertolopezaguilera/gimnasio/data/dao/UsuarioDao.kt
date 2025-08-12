package com.robertolopezaguilera.gimnasio.data.dao

import androidx.room.*
import com.robertolopezaguilera.gimnasio.data.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun getUsuarios(): Flow<List<Usuario>>

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun getCount(): Int

    @Query("SELECT * FROM usuarios WHERE id = :usuarioId LIMIT 1")
    suspend fun getByIdDirecto(usuarioId: String): Usuario?


    @Query("SELECT * FROM usuarios")
    fun getUsuariosFlow(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun getUsuarioPorId(id: String): Flow<Usuario?>

    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios")
    fun getAllUsuariosSinFlow(): List<Usuario>

    @Insert
    suspend fun insert(usuario: Usuario)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usuarios: List<Usuario>)

    @Query("DELETE FROM usuarios")
    suspend fun clearAll()

    @Update
    suspend fun update(usuario: Usuario)

    @Delete
    suspend fun delete(usuario: Usuario)

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun deleteByIds(id: List<String>)

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminarPorId(id: String)

    @Query("SELECT * FROM usuarios WHERE fechaInscripcion = :fecha")
    fun obtenerInscripcionesPorFechaInscripcion(fecha: String): Flow<List<Usuario>>

    // Distribución por género (para gráfico de pastel)
    @Query("SELECT * FROM usuarios WHERE genero = :genero")
    fun getDistribucionGenero(genero: String): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE fechaInscripcion LIKE :mes || '%'")
    fun getUsuariosPorMes(mes: String): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE fechaInscripcion LIKE :mes || '%' AND genero = :genero")
    fun getUsuariosPorMesGenero(mes: String, genero: String): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE fechaInscripcion LIKE :mes || '%' AND experiencia = :experiencia")
    fun getUsuariosPorMesExperiencia(mes: String, experiencia: String): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE fechaInscripcion LIKE :año || '%'")
    fun getUsuariosPorAño(año: String): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE lastUpdated > :ts")
    suspend fun getUpdatedSince(ts: Long): List<Usuario>
}
