package com.example.gimnasio.data.db

import com.example.gimnasio.data.model.FirestoreSyncable

fun <T : FirestoreSyncable> mergeRemoteAndLocal(
    localList: List<T>,
    remoteList: List<T>
): List<T> {
    val localMap = localList.associateBy { it.id }
    val remoteMap = remoteList.associateBy { it.id }

    val allIds = localMap.keys + remoteMap.keys

    return allIds.mapNotNull { id ->
        val local = localMap[id]
        val remote = remoteMap[id]

        when {
            local == null -> remote
            remote == null -> local
            remote.lastUpdated > local.lastUpdated -> remote
            else -> local
        }
    }
}
