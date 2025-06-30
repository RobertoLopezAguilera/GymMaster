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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreSyncService(
    private val usuarioDao: UsuarioDao,
    private val membresiaDao: MembresiaDao,
    private val inscripcionDao: InscripcionDao
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    fun backupAll(onComplete: () -> Unit = {}) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Usuarios
                val usuarios = usuarioDao.getAllUsuarios().first()
                usuarios.forEach { usuario ->
                    firestore.collection("usuarios_data")
                        .document(uid)
                        .collection("usuarios")
                        .document(usuario.id.toString())
                        .set(usuario).await()
                }

                // Membresías
                val membresias = membresiaDao.getAll().first()
                membresias.forEach { membresia ->
                    firestore.collection("usuarios_data")
                        .document(uid)
                        .collection("membresias")
                        .document(membresia.id.toString())
                        .set(membresia).await()
                }

                // Inscripciones
                val inscripciones = inscripcionDao.getAll().first()
                inscripciones.forEach { inscripcion ->
                    firestore.collection("usuarios_data")
                        .document(uid)
                        .collection("inscripciones")
                        .document(inscripcion.id.toString())
                        .set(inscripcion).await()
                }

                withContext(Dispatchers.Main) {
                    onComplete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onComplete()
                }
            }
        }
    }

    fun restoreAll(onComplete: (Boolean) -> Unit = {}) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        var successCount = 0
        val totalCollections = 3

        fun checkComplete() {
            successCount++
            if (successCount == totalCollections) {
                onComplete(true)
            }
        }

        firestore.collection("usuarios_data").document(uid).collection("usuarios")
            .get().addOnSuccessListener { result ->
                val lista = result.toObjects(Usuario::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    usuarioDao.clearAll()
                    usuarioDao.insertAll(lista)
                    checkComplete()
                }
            }.addOnFailureListener {
                onComplete(false)
            }

        firestore.collection("usuarios_data").document(uid).collection("membresias")
            .get().addOnSuccessListener { result ->
                val lista = result.toObjects(Membresia::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    membresiaDao.clearAll()
                    membresiaDao.insertAll(lista)
                    checkComplete()
                }
            }.addOnFailureListener {
                onComplete(false)
            }

        firestore.collection("usuarios_data").document(uid).collection("inscripciones")
            .get().addOnSuccessListener { result ->
                val lista = result.toObjects(Inscripcion::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    inscripcionDao.clearAll()
                    inscripcionDao.insertAll(lista)
                    checkComplete()
                }
            }.addOnFailureListener {
                onComplete(false)
            }
    }
}
