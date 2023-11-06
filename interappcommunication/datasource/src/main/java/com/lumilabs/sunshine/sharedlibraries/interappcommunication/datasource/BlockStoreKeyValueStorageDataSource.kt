package com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource

import android.content.Context
import com.google.android.gms.auth.blockstore.Blockstore
import com.google.android.gms.auth.blockstore.RetrieveBytesRequest
import com.google.android.gms.auth.blockstore.RetrieveBytesResponse
import com.google.android.gms.auth.blockstore.StoreBytesData
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.infrastructure.BackupEnabledKeyValueStorageDataSource
import kotlinx.coroutines.CompletableDeferred

class BlockStoreKeyValueStorageDataSource(
    context: Context
) : BackupEnabledKeyValueStorageDataSource {

    private val client = Blockstore.getClient(context)

    override suspend fun saveString(
        key: String,
        value: String,
        requireEndToEndEncryption: Boolean
    ): Boolean {
        val valueBytes = value.encodeToByteArray()

        return if (requireEndToEndEncryption) {
            saveBytesIfEndToEndEncryptionAvailable(key, valueBytes)
        } else {
            saveBytes(key, valueBytes)
        }
    }

    override suspend fun readString(key: String): String? {
        val retrieveRequest = RetrieveBytesRequest.Builder()
            .setKeys(listOf(key))
            .build()

        val resultDeferred = CompletableDeferred<String?>()

        client.retrieveBytes(retrieveRequest)
            .addOnSuccessListener { result: RetrieveBytesResponse ->
                val blockStoreDataMap =
                    result.blockstoreDataMap

                resultDeferred.complete(blockStoreDataMap[key]?.let { String(it.bytes) })
            }
            .addOnFailureListener { e: Exception? ->
                e?.let {
                    resultDeferred.completeExceptionally(e)
                } ?: run {
                    resultDeferred.complete(null)
                }
            }

        return resultDeferred.await()
    }

    private suspend fun saveBytesIfEndToEndEncryptionAvailable(
        key: String,
        valueBytes: ByteArray
    ): Boolean {
        val canSave = CompletableDeferred<Boolean>()

        client.isEndToEndEncryptionAvailable
            .addOnSuccessListener {
                canSave.complete(it)
            }.addOnFailureListener {
                canSave.complete(false)
            }

        return if (canSave.await()) {
            saveBytes(key, valueBytes)
        } else {
            false
        }
    }

    private suspend fun saveBytes(key: String, value: ByteArray): Boolean {
        val storeBytesDataBuilder = StoreBytesData.Builder()
            .setKey(key)
            .setBytes(value)
            .setShouldBackupToCloud(true)

        val isSaveSuccessfulCompletableDeferred = CompletableDeferred<Boolean>()

        client.storeBytes(storeBytesDataBuilder.build())
            .addOnSuccessListener {
                isSaveSuccessfulCompletableDeferred.complete(true)
            }.addOnFailureListener {
                isSaveSuccessfulCompletableDeferred.complete(false)
            }

        return isSaveSuccessfulCompletableDeferred.await()
    }
}