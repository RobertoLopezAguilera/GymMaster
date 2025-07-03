package com.example.gimnasio.worker

import android.content.Context
import android.util.Log
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

    constructor(context: Context, workerParams: WorkerParameters)
            : this(context, workerParams, null, null, null)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid.isNullOrBlank()) {
                Log.w("AutoBackupWorker", "No hay sesión activa.")
                return@withContext Result.retry()
            }

            val db = AppDatabase.getDatabase(applicationContext)
            val usuarios = usuarioDao ?: db.usuarioDao()
            val membresias = membresiaDao ?: db.membresiaDao()
            val inscripciones = inscripcionDao ?: db.inscripcionDao()

            val syncService = FirestoreSyncService(usuarios, membresias, inscripciones)

            Log.d("AutoBackupWorker", "Iniciando respaldo local → Firestore...")
            val backupSuccess = syncService.backupAllWithResult()

            if (!backupSuccess) {
                Log.e("AutoBackupWorker", "Error en backup. Reintentando...")
                return@withContext Result.retry()
            }

            Log.d("AutoBackupWorker", "Respaldo exitoso. Sincronizando Firestore → local...")
            val syncSuccess = syncService.syncAllDevicesData(uid)

            if (syncSuccess) {
                Log.i("AutoBackupWorker", "Sincronización exitosa.")
                Result.success()
            } else {
                Log.e("AutoBackupWorker", "Fallo al sincronizar datos. Reintentando...")
                Result.retry()
            }

        } catch (e: Exception) {
            Log.e("AutoBackupWorker", "Excepción en Worker: ${e.localizedMessage}")
            Result.retry()
        }
    }

    companion object {
        fun schedulePeriodicWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<AutoBackupWorker>(4, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInitialDelay(15, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "auto_backup_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        fun cancelWork(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork("auto_backup_work")
        }
    }
}
