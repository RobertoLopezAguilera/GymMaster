package com.example.gimnasio.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.gimnasio.data.AppDatabase
import com.example.gimnasio.data.dao.InscripcionDao
import com.example.gimnasio.data.dao.MembresiaDao
import com.example.gimnasio.data.dao.UsuarioDao
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.data.model.Membresia
import com.example.gimnasio.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
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

            // 1. Respaldar datos locales a Firestore
            backupDataToFirestore(uid, usuarioDao, membresiaDao, inscripcionDao)

            // 2. Sincronizar datos entre dispositivos (descargar cambios de otros)
            syncDataFromFirestore(uid, usuarioDao, membresiaDao, inscripcionDao)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun backupDataToFirestore(
        uid: String,
        usuarioDao: UsuarioDao,
        membresiaDao: MembresiaDao,
        inscripcionDao: InscripcionDao
    ) {
        // Respaldar usuarios
        val usuarios = usuarioDao.getAllUsuariosSinFlow()
        usuarios.forEach { usuario ->
            firestore.collection("usuarios_data")
                .document(uid)
                .collection("usuarios")
                .document(usuario.id.toString())
                .set(usuario)
                .await()
        }

        // Respaldar membresías
        val membresias = membresiaDao.getAllSinFlow()
        membresias.forEach { membresia ->
            firestore.collection("usuarios_data")
                .document(uid)
                .collection("membresias")
                .document(membresia.id.toString())
                .set(membresia)
                .await()
        }

        // Respaldar inscripciones
        val inscripciones = inscripcionDao.getAllSinFlow()
        inscripciones.forEach { inscripcion ->
            firestore.collection("usuarios_data")
                .document(uid)
                .collection("inscripciones")
                .document(inscripcion.id.toString())
                .set(inscripcion)
                .await()
        }
    }

    private suspend fun syncDataFromFirestore(
        uid: String,
        usuarioDao: UsuarioDao,
        membresiaDao: MembresiaDao,
        inscripcionDao: InscripcionDao
    ) {
        // Obtener datos de otros dispositivos y mezclarlos
        val otherDevicesData = firestore.collection("usuarios_data")
            .get()
            .await()

        for (deviceDoc in otherDevicesData.documents) {
            if (deviceDoc.id != uid) { // Excluir el propio dispositivo
                // Sincronizar usuarios
                val usuarios = deviceDoc.reference.collection("usuarios").get().await()
                usuarioDao.insertAll(usuarios.toObjects(Usuario::class.java))

                // Sincronizar membresías
                val membresias = deviceDoc.reference.collection("membresias").get().await()
                membresiaDao.insertAll(membresias.toObjects(Membresia::class.java))

                // Sincronizar inscripciones
                val inscripciones = deviceDoc.reference.collection("inscripciones").get().await()
                inscripcionDao.insertAll(inscripciones.toObjects(Inscripcion::class.java))
            }
        }
    }

    companion object {
        fun schedulePeriodicWork(context: Context) {
            val constraints = androidx.work.Constraints.Builder()
                .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                .build()

            val workRequest: PeriodicWorkRequest =
                PeriodicWorkRequestBuilder<AutoBackupWorker>(5, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .setInputData(workDataOf("tag" to "auto_backup"))
                    .build()

            androidx.work.WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "auto_backup_work",
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
        }

        fun cancelWork(context: Context) {
            androidx.work.WorkManager.getInstance(context)
                .cancelUniqueWork("auto_backup_work")
        }
    }
}