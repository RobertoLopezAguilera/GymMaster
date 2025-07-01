package com.example.gimnasio.data.db

import com.example.gimnasio.data.dao.InscripcionDao
import com.example.gimnasio.data.dao.MembresiaDao
import com.example.gimnasio.data.dao.UsuarioDao
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.data.model.Membresia
import com.example.gimnasio.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    fun backupAll(onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            onComplete(false, "Usuario no autenticado")
            return
        }

        scope.launch {
            try {
                // Usuarios
                val usuarios = usuarioDao.getAllUsuariosSinFlow()
                usuarios.forEach { usuario ->
                    try {
                        firestore.collection("usuarios_data")
                            .document(uid)
                            .collection("usuarios")
                            .document(usuario.id.toString())
                            .set(usuario)
                            .await()
                    } catch (e: Exception) {
                        onComplete(false, "Error en usuarios: ${e.message}")
                        return@launch
                    }
                }

                // Membresías
                val membresias = membresiaDao.getAllSinFlow()
                membresias.forEach { membresia ->
                    try {
                        firestore.collection("usuarios_data")
                            .document(uid)
                            .collection("membresias")
                            .document(membresia.id.toString())
                            .set(membresia)
                            .await()
                    } catch (e: Exception) {
                        onComplete(false, "Error en membresías: ${e.message}")
                        return@launch
                    }
                }

                // Inscripciones
                val inscripciones = inscripcionDao.getAllSinFlow()
                inscripciones.forEach { inscripcion ->
                    try {
                        firestore.collection("usuarios_data")
                            .document(uid)
                            .collection("inscripciones")
                            .document(inscripcion.id.toString())
                            .set(inscripcion)
                            .await()
                    } catch (e: Exception) {
                        onComplete(false, "Error en inscripciones: ${e.message}")
                        return@launch
                    }
                }

                onComplete(true, "Respaldo completado con éxito")
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false, "Error general: ${e.message}")
            }
        }
    }

    fun restoreAll(onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            onComplete(false, "Usuario no autenticado")
            return
        }

        var successCount = 0
        var errorMessage: String? = null
        val totalCollections = 3

        fun checkComplete() {
            successCount++
            if (successCount == totalCollections) {
                onComplete(errorMessage == null, errorMessage)
            }
        }

        // Restaurar usuarios
        firestore.collection("usuarios_data").document(uid).collection("usuarios")
            .get().addOnSuccessListener { result ->
                scope.launch {
                    try {
                        val lista = result.toObjects(Usuario::class.java)
                        usuarioDao.clearAll()
                        usuarioDao.insertAll(lista)
                        checkComplete()
                    } catch (e: Exception) {
                        errorMessage = "Error restaurando usuarios: ${e.message}"
                        checkComplete()
                    }
                }
            }.addOnFailureListener {
                errorMessage = "Error obteniendo usuarios: ${it.message}"
                checkComplete()
            }

        // Restaurar membresías
        firestore.collection("usuarios_data").document(uid).collection("membresias")
            .get().addOnSuccessListener { result ->
                scope.launch {
                    try {
                        val lista = result.toObjects(Membresia::class.java)
                        membresiaDao.clearAll()
                        membresiaDao.insertAll(lista)
                        checkComplete()
                    } catch (e: Exception) {
                        errorMessage = "Error restaurando membresías: ${e.message}"
                        checkComplete()
                    }
                }
            }.addOnFailureListener {
                errorMessage = "Error obteniendo membresías: ${it.message}"
                checkComplete()
            }

        // Restaurar inscripciones
        firestore.collection("usuarios_data").document(uid).collection("inscripciones")
            .get().addOnSuccessListener { result ->
                scope.launch {
                    try {
                        val lista = result.toObjects(Inscripcion::class.java)
                        inscripcionDao.clearAll()
                        inscripcionDao.insertAll(lista)
                        checkComplete()
                    } catch (e: Exception) {
                        errorMessage = "Error restaurando inscripciones: ${e.message}"
                        checkComplete()
                    }
                }
            }.addOnFailureListener {
                errorMessage = "Error obteniendo inscripciones: ${it.message}"
                checkComplete()
            }
    }

    suspend fun backupAllWithResult(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            backupAll { success, message ->
                continuation.resume(success)
            }
        }
    }

    suspend fun restoreAllWithResult(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            restoreAll { success, message ->
                continuation.resume(success)
            }
        }
    }

    fun cancelOperations() {
        scope.cancel()
    }
}