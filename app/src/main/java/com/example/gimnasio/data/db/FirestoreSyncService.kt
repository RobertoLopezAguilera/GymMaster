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

class FirestoreSyncService(
    private val usuarioDao: UsuarioDao,
    private val membresiaDao: MembresiaDao,
    private val inscripcionDao: InscripcionDao
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    fun backupAll() {
        if (uid == null) return

        CoroutineScope(Dispatchers.IO).launch {
            // Usuarios
            val usuarios = usuarioDao.getAllUsuarios().first()
            usuarios.forEach { usuario ->
                firestore.collection("usuarios")
                    .document(uid)
                    .collection("usuarios_locales")
                    .document(usuario.id.toString())
                    .set(usuario)
            }

            // Membresías
            val membresias = membresiaDao.getAll().first()
            membresias.forEach { membresia ->
                firestore.collection("usuarios")
                    .document(uid)
                    .collection("membresias_locales")
                    .document(membresia.id.toString())
                    .set(membresia)
            }

            // Inscripciones
            val inscripciones = inscripcionDao.getAll().first()
            inscripciones.forEach { inscripcion ->
                firestore.collection("usuarios")
                    .document(uid)
                    .collection("inscripciones_locales")
                    .document(inscripcion.id.toString())
                    .set(inscripcion)
            }
        }
    }

    fun restoreAll() {
        if (uid == null) return

        CoroutineScope(Dispatchers.IO).launch {
            // Restaurar Usuarios
            firestore.collection("usuarios").document(uid).collection("usuarios_locales")
                .get().addOnSuccessListener { result ->
                    val lista = result.toObjects(Usuario::class.java)
                    CoroutineScope(Dispatchers.IO).launch {
                        usuarioDao.clearAll()
                        usuarioDao.insertAll(lista)
                    }
                }

            // Restaurar Membresías
            firestore.collection("usuarios").document(uid).collection("membresias_locales")
                .get().addOnSuccessListener { result ->
                    val lista = result.toObjects(Membresia::class.java)
                    CoroutineScope(Dispatchers.IO).launch {
                        membresiaDao.clearAll()
                        membresiaDao.insertAll(lista)
                    }
                }

            // Restaurar Inscripciones
            firestore.collection("usuarios").document(uid).collection("inscripciones_locales")
                .get().addOnSuccessListener { result ->
                    val lista = result.toObjects(Inscripcion::class.java)
                    CoroutineScope(Dispatchers.IO).launch {
                        inscripcionDao.clearAll()
                        inscripcionDao.insertAll(lista)
                    }
                }
        }
    }
}
