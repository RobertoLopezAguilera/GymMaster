package com.example.gimnasio.data.dao

import androidx.room.*
import com.example.gimnasio.data.model.Membresia
import kotlinx.coroutines.flow.Flow

@Dao
interface MembresiaDao {
    @Query("SELECT * FROM membresias")
    fun getAll(): Flow<List<Membresia>>

    @Query("SELECT * FROM membresias")
    fun getAllSinFlow(): List<Membresia>

    @Query("SELECT * FROM membresias WHERE id = :id")
    fun getById(id: Int): Flow<Membresia?>

    @Query("SELECT COUNT(*) FROM membresias")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(membresia: Membresia)

    @Update
    suspend fun update(membresia: Membresia)

    @Delete
    suspend fun delete(membresia: Membresia)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(membresias: List<Membresia>)

    @Query("DELETE FROM membresias")
    suspend fun clearAll()

}
