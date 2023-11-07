package com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource

import android.content.ContentResolver
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource.converters.convertToInterAppCredentials
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.infrastructure.BackupEnabledKeyValueStorageDataSource
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.infrastructure.InterAppCredentialsRepository
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentials
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.CONTACT_USER_ID
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.FIREBASE_TOKEN
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.FIREBASE_USER_ID
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.PHONE_NUMBER_OR_FIREBASE_USER_ID
import kotlinx.coroutines.tasks.await

class InterAppCredentialsRepositoryImpl(
    private val storageDataSource: BackupEnabledKeyValueStorageDataSource,
    private val contentResolver: ContentResolver,
) : InterAppCredentialsRepository {

    override suspend fun retrieveInterAppCredentials(
        packageNames: List<String>
    ): InterAppCredentials? {
        val fromStorage = retrieveCredentialsFromStorageDataSource()
        if (fromStorage != null) {
            return fromStorage
        }
        for (name in packageNames) {
            val fromContentResolver = retrieveCredentialsFromContentResolver(name)
            if (fromContentResolver != null) {
                return fromContentResolver
            }
        }

        return null
    }

    override suspend fun saveInterAppCredentials(
        contactUserId: String?,
        contactPrimaryMobile: String?
    ) {
        try {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val firebaseToken = firebaseUser?.getIdToken(false)?.await()?.token
            val firebaseUserId = firebaseUser?.uid

            val phoneNumberOrFirebaseUserId = contactPrimaryMobile ?: firebaseUserId

            // TODO: Test enabling end to end encryption
            storageDataSource.apply {
                saveString(
                    CONTACT_USER_ID.value,
                    contactUserId!!,
                    requireEndToEndEncryption = false
                )
                saveString(
                    FIREBASE_USER_ID.value,
                    firebaseUserId!!,
                    requireEndToEndEncryption = false
                )
                saveString(
                    FIREBASE_TOKEN.value,
                    firebaseToken!!,
                    requireEndToEndEncryption = false
                )
                saveString(
                    PHONE_NUMBER_OR_FIREBASE_USER_ID.value,
                    phoneNumberOrFirebaseUserId!!,
                    requireEndToEndEncryption = false
                )
            }
        } catch (e: Exception) {
            // TODO: Define how to handle InAppCredentials errors
        }
    }

    private fun retrieveCredentialsFromContentResolver(
        packageName: String,
    ): InterAppCredentials? {
        return contentResolver.query(
            // TODO: Fill uriString dynamically
            Uri.parse("content://$packageName.InterAppContentProvider/access"),
            null, null, null, null
        )?.use { it.convertToInterAppCredentials() }
    }

    private suspend fun retrieveCredentialsFromStorageDataSource(): InterAppCredentials? {
        return try {
            val contactUserId = storageDataSource.readString(CONTACT_USER_ID.value)
            val firebaseUserId = storageDataSource.readString(FIREBASE_USER_ID.value)
            val firebaseToken = storageDataSource.readString(FIREBASE_TOKEN.value)
            val phoneNumberOrFirebaseUserId =
                storageDataSource.readString(PHONE_NUMBER_OR_FIREBASE_USER_ID.value)
            InterAppCredentials(
                contactUserId = contactUserId!!,
                firebaseUserId = firebaseUserId!!,
                firebaseToken = firebaseToken!!,
                phoneNumberOrFirebaseUserId = phoneNumberOrFirebaseUserId!!
            )
        } catch (e: Exception) {
            // TODO: Define how to handle InAppCredentials errors
            null
        }
    }
}