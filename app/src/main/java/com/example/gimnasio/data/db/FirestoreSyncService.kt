package com.example.gimnasio.data.db

import android.util.Log
import com.example.gimnasio.data.dao.InscripcionDao
import com.example.gimnasio.data.dao.MembresiaDao
import com.example.gimnasio.data.dao.UsuarioDao
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.data.model.Membresia
import com.example.gimnasio.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class FirestoreSyncService(
    private val usuarioDao: UsuarioDao,
    private val membresiaDao: MembresiaDao,
    private val inscripcionDao: InscripcionDao
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    suspend fun checkUserDataExists(uid: String): Boolean {
        return try {
            val snapshot = firestore.collection("usuarios_data")
                .document(uid)
                .collection("usuarios")
                .limit(1)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    suspend fun backupAll(): Boolean = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Log.e("FirestoreBackup", "Usuario no autenticado")
            return@withContext false
        }

        try {
            Log.d("FirestoreBackup", "Iniciando respaldo...")

            val usuariosLocales = usuarioDao.getAllUsuariosSinFlow()
            val membresiasLocales = membresiaDao.getAllSinFlow()
            val inscripcionesLocales = inscripcionDao.getAllSinFlow()

            // === Usuarios ===
            val usuariosRef = firestore.collection("usuarios_data").document(uid).collection("usuarios")
            val usuariosRemotos = usuariosRef.get().await().documents.map { it.id }

            val usuariosEliminados = usuariosRemotos.filterNot { id -> usuariosLocales.any { it.id == id } }
            Log.d("FirestoreBackup", "Usuarios eliminados: $usuariosEliminados")
            usuariosEliminados.forEach {
                usuariosRef.document(it).delete().await()
                Log.d("FirestoreBackup", "Usuario eliminado en Firestore: $it")
            }

            usuariosLocales.forEach {
                usuariosRef.document(it.id).set(it, SetOptions.merge()).await()
                Log.d("FirestoreBackup", "Usuario respaldado: ${it.id}")
            }

            // === Membresías ===
            val membresiasRef = firestore.collection("usuarios_data").document(uid).collection("membresias")
            val membresiasRemotas = membresiasRef.get().await().documents.map { it.id }

            val membresiasEliminadas = membresiasRemotas.filterNot { id -> membresiasLocales.any { it.id == id } }
            Log.d("FirestoreBackup", "Membresías eliminadas: $membresiasEliminadas")
            membresiasEliminadas.forEach {
                membresiasRef.document(it).delete().await()
                Log.d("FirestoreBackup", "Membresía eliminada en Firestore: $it")
            }

            membresiasLocales.forEach {
                membresiasRef.document(it.id).set(it, SetOptions.merge()).await()
                Log.d("FirestoreBackup", "Membresía respaldada: ${it.id}")
            }

            // === Inscripciones ===
            val inscripcionesRef = firestore.collection("usuarios_data").document(uid).collection("inscripciones")
            val inscripcionesRemotas = inscripcionesRef.get().await().documents.map { it.id }

            val inscripcionesEliminadas = inscripcionesRemotas.filterNot { id -> inscripcionesLocales.any { it.id == id } }
            Log.d("FirestoreBackup", "Inscripciones eliminadas: $inscripcionesEliminadas")
            inscripcionesEliminadas.forEach {
                inscripcionesRef.document(it).delete().await()
                Log.d("FirestoreBackup", "Inscripción eliminada en Firestore: $it")
            }

            inscripcionesLocales.forEach {
                inscripcionesRef.document(it.id).set(it, SetOptions.merge()).await()
                Log.d("FirestoreBackup", "Inscripción respaldada: ${it.id}")
            }

            Log.d("FirestoreBackup", "Respaldo completado correctamente.")
            true

        } catch (e: Exception) {
            Log.e("FirestoreBackup", "Error durante el respaldo: ${e.localizedMessage}", e)
            false
        }
    }



    suspend fun restoreAll(): Boolean = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@withContext false

        return@withContext try {
            val usuariosSnap = firestore.collection("usuarios_data").document(uid).collection("usuarios").get().await()
            val membresiasSnap = firestore.collection("usuarios_data").document(uid).collection("membresias").get().await()
            val inscripcionesSnap = firestore.collection("usuarios_data").document(uid).collection("inscripciones").get().await()

            val usuarios = usuariosSnap.toObjects(Usuario::class.java)
            val membresias = membresiasSnap.toObjects(Membresia::class.java)
            val inscripciones = inscripcionesSnap.toObjects(Inscripcion::class.java)

            usuarioDao.clearAll()
            usuarioDao.insertAll(usuarios)

            membresiaDao.clearAll()
            membresiaDao.insertAll(membresias)

            inscripcionDao.clearAll()
            inscripcionDao.insertAll(inscripciones)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun backupAllWithResult(): Boolean = backupAll()

    suspend fun restoreAllWithResult(): Boolean = restoreAll()

    suspend fun syncAllDevicesData(uid: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Paso 1: Respaldar datos locales al Firestore
            if (!backupAll()) return@withContext false

            // Paso 2: Descargar datos de otros dispositivos
            val allDevices = firestore.collection("usuarios_data").get().await()

            for (deviceDoc in allDevices.documents) {
                val deviceUid = deviceDoc.id
                if (deviceUid == uid) continue

                // === Usuarios ===
                val usuariosRemotos = firestore.collection("usuarios_data")
                    .document(deviceUid).collection("usuarios")
                    .get().await().toObjects(Usuario::class.java)

                val usuariosLocales = usuarioDao.getAllUsuariosSinFlow().associateBy { it.id }
                val nuevosUsuarios = usuariosRemotos.filter {
                    it.id !in usuariosLocales || it.lastUpdated > (usuariosLocales[it.id]?.lastUpdated ?: 0L)
                }
                usuarioDao.insertAll(nuevosUsuarios)

                // === Membresías ===
                val membresiasRemotas = firestore.collection("usuarios_data")
                    .document(deviceUid).collection("membresias")
                    .get().await().toObjects(Membresia::class.java)

                val membresiasLocales = membresiaDao.getAllSinFlow().associateBy { it.id }
                val nuevasMembresias = membresiasRemotas.filter {
                    it.id !in membresiasLocales || it.lastUpdated > (membresiasLocales[it.id]?.lastUpdated ?: 0L)
                }
                membresiaDao.insertAll(nuevasMembresias)

                // === Inscripciones ===
                val inscripcionesRemotas = firestore.collection("usuarios_data")
                    .document(deviceUid).collection("inscripciones")
                    .get().await().toObjects(Inscripcion::class.java)

                val inscripcionesLocales = inscripcionDao.getAllSinFlow().associateBy { it.id }
                val nuevasInscripciones = inscripcionesRemotas.filter {
                    it.id !in inscripcionesLocales || it.lastUpdated > (inscripcionesLocales[it.id]?.lastUpdated ?: 0L)
                }
                inscripcionDao.insertAll(nuevasInscripciones)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun cancelOperations() {
        scope.cancel()
    }
}