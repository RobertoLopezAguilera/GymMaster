package com.example.gimnasio.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.gimnasio.data.AppDatabase
import com.example.gimnasio.data.dao.InscripcionDao
import com.example.gimnasio.data.dao.MembresiaDao
import com.example.gimnasio.data.dao.UsuarioDao
import com.example.gimnasio.data.db.FirestoreSyncService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AutoBackupWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val usuarioDao: UsuarioDao? = null,
    private val membresiaDao: MembresiaDao? = null,
    private val inscripcionDao: InscripcionDao? = null
) : CoroutineWorker(context, workerParams) {

    private val firestore = FirebaseFirestore.getInstance()

    // Constructor alternativo para inyección de dependencias en tests
    constructor(context: Context, workerParams: WorkerParameters) : this(context, workerParams, null, null, null)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                return@withContext Result.retry()
            }

            // Obtener instancias de DAO si no fueron inyectadas
            val db = AppDatabase.getDatabase(applicationContext)
            val usuarioDao = usuarioDao ?: db.usuarioDao()
            val membresiaDao = membresiaDao ?: db.membresiaDao()
            val inscripcionDao = inscripcionDao ?: db.inscripcionDao()

            // Crear instancia del servicio de sincronización
            val syncService = FirestoreSyncService(usuarioDao, membresiaDao, inscripcionDao)

            // 1. Realizar respaldo completo
            val backupSuccess = syncService.backupAllWithResult()
            if (!backupSuccess) {
                return@withContext Result.retry()
            }

            // 2. Sincronizar datos entre dispositivos
            val syncSuccess = syncService.syncAllDevicesData(uid)

            if (syncSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        fun schedulePeriodicWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<AutoBackupWorker>(
                5, // Cada 5 horas
                TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.HOURS) // Esperar 1 hora antes del primer trabajo
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "auto_backup_work",
                ExistingPeriodicWorkPolicy.UPDATE, // Actualiza si ya existe
                workRequest
            )
        }

        fun cancelWork(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork("auto_backup_work")
        }
    }
}