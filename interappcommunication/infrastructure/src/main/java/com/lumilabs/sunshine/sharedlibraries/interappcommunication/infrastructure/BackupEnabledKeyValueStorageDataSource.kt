package com.lumilabs.sunshine.sharedlibraries.interappcommunication.infrastructure

interface BackupEnabledKeyValueStorageDataSource {
    suspend fun saveString(key: String, value: String, requireEndToEndEncryption: Boolean): Boolean
    suspend fun readString(key: String): String?
}