package com.example.gimnasio.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gimnasio.data.AppDatabase
import com.example.gimnasio.data.db.FirestoreSyncService

class FirestoreBackupWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getDatabase(applicationContext)
            val syncService = FirestoreSyncService(
                db.usuarioDao(),
                db.membresiaDao(),
                db.inscripcionDao()
            )

            syncService.backupAll()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
