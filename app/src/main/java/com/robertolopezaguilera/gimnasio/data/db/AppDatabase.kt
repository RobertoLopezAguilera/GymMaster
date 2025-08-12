package com.robertolopezaguilera.gimnasio.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.robertolopezaguilera.gimnasio.data.dao.*
import com.robertolopezaguilera.gimnasio.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(
    entities = [Usuario::class, Membresia::class, Inscripcion::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun membresiaDao(): MembresiaDao
    abstract fun inscripcionDao(): InscripcionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calabozogym_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
        suspend fun <T> runInTransaction(block: suspend () -> T): T {
            return withContext(Dispatchers.IO) {
                runInTransaction {
                    block()
                }
            }
        }
    }
}
