package com.robertolopezaguilera.gimnasio.data.db

import android.content.Context
import android.util.Log
import com.robertolopezaguilera.gimnasio.data.dao.InscripcionDao
import com.robertolopezaguilera.gimnasio.data.dao.MembresiaDao
import com.robertolopezaguilera.gimnasio.data.dao.UsuarioDao
import com.robertolopezaguilera.gimnasio.data.model.FirestoreSyncable
import com.robertolopezaguilera.gimnasio.data.model.Inscripcion
import com.robertolopezaguilera.gimnasio.data.model.Membresia
import com.robertolopezaguilera.gimnasio.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreSyncService(
    private val usuarioDao: UsuarioDao,
    private val membresiaDao: MembresiaDao,
    private val inscripcionDao: InscripcionDao,
    private val context: Context
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
    private var lastSync: Long
        get() = prefs.getLong("last_sync", 0L)
        set(value) = prefs.edit().putLong("last_sync", value).apply()

    // Activa para forzar sincronización completa
    private val forceFullSync = false

    suspend fun twoWaySync(uid: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("Sync", "📤 Iniciando backupAll() para reflejar eliminaciones en Firestore")
            val backupOk = backupAll()
            if (!backupOk) {
                Log.e("Sync", "❌ backupAll() falló. Abortando sincronización.")
                return@withContext false
            }

            val root = firestore.collection("usuarios_data").document(uid)

            val pullUsuarios = root.collection("usuarios")
            val pullMembresias = root.collection("membresias")
            val pullInscripciones = root.collection("inscripciones")

            val syncTime = if (forceFullSync) 0L else lastSync
            Log.d("Sync", "🔄 Iniciando twoWaySync. LastSync = $syncTime")

            // 1. PULL (descargar de Firestore)
            val usuariosCloud = pullUsuarios.updatedAfter(syncTime, Usuario::class.java)
            val membCloud = pullMembresias.updatedAfter(syncTime, Membresia::class.java)
            val inscCloud = pullInscripciones.updatedAfter(syncTime, Inscripcion::class.java)

            Log.d("Sync", "📥 Descargados: ${usuariosCloud.size} usuarios, ${membCloud.size} membresías, ${inscCloud.size} inscripciones")

            // 2. MERGE: Insertar nuevos/actualizados
            mergeToRoom("usuarios", usuariosCloud, usuarioDao.getAllUsuariosSinFlow(), usuarioDao::insertAll)
            mergeToRoom("membresías", membCloud, membresiaDao.getAllSinFlow(), membresiaDao::insertAll)
            mergeToRoom("inscripciones", inscCloud, inscripcionDao.getAllSinFlow(), inscripcionDao::insertAll)

            // 🗑️ 3. ELIMINAR datos locales que ya no existen en Firestore
            syncDeletions(pullUsuarios, usuarioDao.getAllUsuariosSinFlow()) { ids ->
                usuarioDao.deleteByIds(ids)
            }
            syncDeletions(pullMembresias, membresiaDao.getAllSinFlow()) { ids ->
                membresiaDao.deleteByIds(ids)
            }
            syncDeletions(pullInscripciones, inscripcionDao.getAllSinFlow()) { ids ->
                inscripcionDao.deleteByIds(ids)
            }

            // 4. PUSH (subir lo que cambió localmente)
            pushIfNewer(usuarioDao.getAllUsuariosSinFlow(), usuariosCloud, pullUsuarios)
            pushIfNewer(membresiaDao.getAllSinFlow(), membCloud, pullMembresias)
            pushIfNewer(inscripcionDao.getAllSinFlow(), inscCloud, pullInscripciones)

            lastSync = System.currentTimeMillis()
            Log.d("Sync", "✅ Sincronización completada. Nuevo lastSync: $lastSync")
            true
        } catch (e: Exception) {
            Log.e("Sync", "❌ Error en twoWaySync", e)
            false
        }
    }


    private suspend fun <T : FirestoreSyncable> syncDeletions(
        collectionRef: CollectionReference,
        localItems: List<T>,
        deleteLocalByIds: suspend (List<String>) -> Unit
    ) {
        try {
            val snapshot = collectionRef.get().await()
            val firestoreIds = snapshot.documents.map { it.id }.toSet()
            val localIds = localItems.map { it.id }

            val idsToDeleteLocally = localIds.filterNot { firestoreIds.contains(it) }

            if (idsToDeleteLocally.isNotEmpty()) {
                Log.d("Sync", "🗑️ Eliminando localmente ${idsToDeleteLocally.size} elementos no presentes en Firestore: $idsToDeleteLocally")
                deleteLocalByIds(idsToDeleteLocally)
            } else {
                Log.d("Sync", "✅ No hay eliminaciones pendientes en local")
            }
        } catch (e: Exception) {
            Log.e("Sync", "❌ Error en syncDeletions", e)
        }
    }

    private suspend fun <T : FirestoreSyncable> mergeToRoom(
        tag: String,
        cloud: List<T>,
        local: List<T>,
        upsert: suspend (List<T>) -> Unit
    ) {
        val localMap = local.associateBy { it.id }

        val toInsertOrUpdate = cloud.filter { remote ->
            val localItem = localMap[remote.id]
            val shouldUpdate = localItem == null || remote.lastUpdated > localItem.lastUpdated

            Log.d("Sync-$tag", "→ Revisando ${remote.id}: local=${localItem?.lastUpdated}, remote=${remote.lastUpdated}, update=$shouldUpdate")
            shouldUpdate
        }

        if (toInsertOrUpdate.isNotEmpty()) {
            Log.d("Sync-$tag", "⬇ Insertando/Actualizando ${toInsertOrUpdate.size} elementos en Room")
            upsert(toInsertOrUpdate)
        } else {
            Log.d("Sync-$tag", "✅ Sin cambios necesarios en Room")
        }
    }

    /* ---------- Helpers ---------- */
    private fun FirebaseFirestore.collection(uid: String, name: String) =
        collection("usuarios_data").document(uid).collection(name)

    private suspend fun <T> CollectionReference.updatedAfter(ts: Long, clazz: Class<T>): List<T> {
        return try {
            val query = if (ts == 0L) this else whereGreaterThan("lastUpdated", ts)
            val snapshot = query.get().await()
            snapshot.toObjects(clazz)
        } catch (e: Exception) {
            Log.e("Sync", "⚠ Error al obtener documentos actualizados", e)
            emptyList()
        }
    }

    suspend private fun CollectionReference.getUpdatedQuery(ts: Long) =
        if (ts == 0L) this else whereGreaterThan("lastUpdated", ts)

    private suspend fun <T : FirestoreSyncable> mergeToRoom(
        cloud: List<T>,
        local: List<T>,
        upsert: suspend (List<T>) -> Unit
    ) {
        val localMap = local.associateBy { it.id }

        val toInsertOrUpdate = cloud.filter { remote ->
            val local = localMap[remote.id]
            local == null || remote.lastUpdated > local.lastUpdated
        }

        Log.d("Sync", "Datos nuevos o actualizados a insertar: ${toInsertOrUpdate.size}")

        if (toInsertOrUpdate.isNotEmpty()) {
            upsert(toInsertOrUpdate)
        }
    }

    private suspend fun <T : FirestoreSyncable> pushIfNewer(
        local: List<T>,
        cloud: List<T>,
        colRef: CollectionReference
    ) {
        val cloudMap = cloud.associateBy { it.id }

        val toPush = local.filter { l ->
            val remote = cloudMap[l.id]
            remote == null || l.lastUpdated > remote.lastUpdated
        }

        if (toPush.isNotEmpty()) {
            Log.d("Sync", "⬆ Subiendo ${toPush.size} documentos a Firestore")
        }

        for (chunk in toPush.chunked(400)) {
            val batch = colRef.firestore.batch()
            chunk.forEach { entity ->
                batch.set(colRef.document(entity.id), entity, SetOptions.merge())
            }
            batch.commit().await()
        }
    }

    suspend fun checkUserDataExists(uid: String): Boolean {
        return try {
            val snapshot = firestore.collection("usuarios_data")
                .document(uid).collection("usuarios")
                .limit(1).get().await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    suspend fun backupAll(): Boolean = withContext(Dispatchers.IO) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@withContext false

        try {
            Log.d("FirestoreBackup", "Iniciando respaldo...")

            val usuariosLocales = usuarioDao.getAllUsuariosSinFlow()
            val membresiasLocales = membresiaDao.getAllSinFlow()
            val inscripcionesLocales = inscripcionDao.getAllSinFlow()

            val usuariosRef = firestore.collection("usuarios_data").document(uid).collection("usuarios")
            val membresiasRef = firestore.collection("usuarios_data").document(uid).collection("membresias")
            val inscripcionesRef = firestore.collection("usuarios_data").document(uid).collection("inscripciones")

            val usuariosRemotos = usuariosRef.get().await().documents.map { it.id }
            val membresiasRemotas = membresiasRef.get().await().documents.map { it.id }
            val inscripcionesRemotas = inscripcionesRef.get().await().documents.map { it.id }

            // Borrar en Firestore lo que ya no existe localmente
            usuariosRemotos.filterNot { id -> usuariosLocales.any { it.id == id } }
                .forEach { id -> usuariosRef.document(id).delete().await() }

            membresiasRemotas.filterNot { id -> membresiasLocales.any { it.id == id } }
                .forEach { id -> membresiasRef.document(id).delete().await() }

            inscripcionesRemotas.filterNot { id -> inscripcionesLocales.any { it.id == id } }
                .forEach { id -> inscripcionesRef.document(id).delete().await() }

            // Subir locales
            usuariosLocales.forEach { usuariosRef.document(it.id).set(it, SetOptions.merge()).await() }
            membresiasLocales.forEach { membresiasRef.document(it.id).set(it, SetOptions.merge()).await() }
            inscripcionesLocales.forEach { inscripcionesRef.document(it.id).set(it, SetOptions.merge()).await() }

            Log.d("FirestoreBackup", "Respaldo completado correctamente.")
            true
        } catch (e: Exception) {
            Log.e("FirestoreBackup", "Error durante backupAll", e)
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
            if (!backupAll()) return@withContext false

            val allDevices = firestore.collection("usuarios_data").get().await()

            for (deviceDoc in allDevices.documents) {
                val deviceUid = deviceDoc.id
                if (deviceUid == uid) continue

                val usuariosRemotos = firestore.collection("usuarios_data")
                    .document(deviceUid).collection("usuarios").get().await().toObjects(Usuario::class.java)

                val membresiasRemotas = firestore.collection("usuarios_data")
                    .document(deviceUid).collection("membresias").get().await().toObjects(Membresia::class.java)

                val inscripcionesRemotas = firestore.collection("usuarios_data")
                    .document(deviceUid).collection("inscripciones").get().await().toObjects(Inscripcion::class.java)

                val usuariosLocales = usuarioDao.getAllUsuariosSinFlow().associateBy { it.id }
                val nuevasUsuarios = usuariosRemotos.filter {
                    it.id !in usuariosLocales || it.lastUpdated > (usuariosLocales[it.id]?.lastUpdated ?: 0L)
                }
                usuarioDao.insertAll(nuevasUsuarios)

                val membresiasLocales = membresiaDao.getAllSinFlow().associateBy { it.id }
                val nuevasMembresias = membresiasRemotas.filter {
                    it.id !in membresiasLocales || it.lastUpdated > (membresiasLocales[it.id]?.lastUpdated ?: 0L)
                }
                membresiaDao.insertAll(nuevasMembresias)

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
}