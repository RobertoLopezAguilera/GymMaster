package com.example.gimnasio.data.dao

import androidx.room.*
import com.example.gimnasio.data.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios")
    fun getAll(): kotlinx.coroutines.flow.Flow<List<Usuario>>

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun getCount(): Int

    @Query("SELECT * FROM usuarios")
    fun getUsuariosFlow(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun getUsuarioPorId(id: Int): Flow<Usuario?>

    @Insert
    suspend fun insert(usuario: Usuario)

    @Update
    suspend fun update(usuario: Usuario)

    @Delete
    suspend fun delete(usuario: Usuario)

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}
