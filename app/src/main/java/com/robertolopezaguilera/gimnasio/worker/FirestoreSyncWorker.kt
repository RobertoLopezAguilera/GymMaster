package com.robertolopezaguilera.gimnasio.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.robertolopezaguilera.gimnasio.R
import com.robertolopezaguilera.gimnasio.data.AppDatabase
import com.robertolopezaguilera.gimnasio.data.db.FirestoreSyncService
import com.google.firebase.auth.FirebaseAuth

class FirestoreSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "sync_channel"
        const val NOTIFICATION_ID = 101
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val user = FirebaseAuth.getInstance().currentUser ?: return Result.failure()

        val db = AppDatabase.getDatabase(applicationContext)
        val syncService = FirestoreSyncService(
            db.usuarioDao(),
            db.membresiaDao(),
            db.inscripcionDao(),
            applicationContext
        )

        // Crear el canal (si no existe)
        createNotificationChannel()

        return try {
            val ok = syncService.twoWaySync(user.uid)

            if (ok) {
                showNotification("Sincronización exitosa", "Los datos se sincronizaron correctamente.")
                Result.success()
            } else {
                showNotification("Sincronización fallida", "Hubo un error al sincronizar los datos.")
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showNotification("Error de sincronización", "Ocurrió un error inesperado.")
            Result.retry()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_backup)  // Usa tu ícono
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Sincronización"
            val descriptionText = "Notificaciones del respaldo automático"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
