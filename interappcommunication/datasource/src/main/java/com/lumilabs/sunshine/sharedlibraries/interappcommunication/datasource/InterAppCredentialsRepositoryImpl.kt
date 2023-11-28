package com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource

import android.content.ContentResolver
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource.converters.convertToInterAppCredentials
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource.formatters.addCountryCodeIfMissing
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.infrastructure.BackupEnabledKeyValueStorageDataSource
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.infrastructure.InterAppCredentialsRepository
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentials
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.CONTACT_PRIMARY_MOBILE
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.CONTACT_USER_ID
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.FIREBASE_TOKEN
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.FIREBASE_USER_ID
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result.DeleteCredentialsResult
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result.RetrieveCredentialsError
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result.RetrieveCredentialsResult
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result.RetrieveCredentialsSuccess
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result.SaveCredentialsResult
import kotlinx.coroutines.tasks.await

class InterAppCredentialsRepositoryImpl(
    private val storageDataSource: BackupEnabledKeyValueStorageDataSource,
    private val contentResolver: ContentResolver,
) : InterAppCredentialsRepository {

    override suspend fun retrieveInterAppCredentials(
        packageNames: List<String>
    ): RetrieveCredentialsResult {
        val errors = mutableMapOf<String, RetrieveCredentialsError>()

        when (val storageResult = retrieveCredentialsFromStorageDataSource()) {
            is RetrieveCredentialsSuccess.FromStorage -> {
                return storageResult
            }

            is RetrieveCredentialsError -> {
                errors.put("keyValueStorage", storageResult)
            }
        }
        for (name in packageNames) {
            when (val resolverResult = retrieveCredentialsFromContentResolver(name)) {
                is RetrieveCredentialsSuccess.FromContentResolver -> {
                    return resolverResult
                }

                is RetrieveCredentialsError -> {
                    errors.put("contentResolver $name", resolverResult)
                }
            }
        }

        return if (errors.isNotEmpty()) RetrieveCredentialsError.MultipleErrors(errors)
        else RetrieveCredentialsError.NoCredentialsFound
    }

    override suspend fun saveInterAppCredentials(
        contactUserId: String?,
        contactPrimaryMobile: String?
    ): SaveCredentialsResult {
        return try {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val firebaseToken = firebaseUser?.getIdToken(true)?.await()?.token
            val firebaseUserId = firebaseUser?.uid

            // It was decided with Vishal not to enforce end-to-end encryption:
            // https://57blocks.slack.com/archives/CRFUY2G14/p1699551274913989
            // This decision is due to constant and common false negatives
            // during our tests in our devices and emulators.
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
                contactPrimaryMobile?.let { primaryMobile ->
                    saveString(
                        CONTACT_PRIMARY_MOBILE.value,
                        primaryMobile,
                        requireEndToEndEncryption = false
                    )
                }
            }
            SaveCredentialsResult.SaveSuccess
        } catch (e: Exception) {
            SaveCredentialsResult.Error(e)
        }
    }

    override suspend fun clearInterAppCredentials(): DeleteCredentialsResult {
        return try {
            if (storageDataSource.clearStrings())
                DeleteCredentialsResult.DeleteSuccess
            else DeleteCredentialsResult.DeleteError(null)
        } catch (e: Exception) {
            DeleteCredentialsResult.DeleteError(e)
        }
    }

    private fun retrieveCredentialsFromContentResolver(
        packageName: String,
    ): RetrieveCredentialsResult {
        return try {
            contentResolver.query(
                Uri.parse("content://$packageName.InterAppContentProvider/access"),
                null, null, null, null
            )?.use { it.convertToInterAppCredentials() }
                ?: RetrieveCredentialsError.NoCredentialsFound
        } catch (e: Exception) {
            RetrieveCredentialsError.ContentResolverPermission(e)
        }
    }

    private suspend fun retrieveCredentialsFromStorageDataSource(): RetrieveCredentialsResult {
        return try {
            val contactUserId = storageDataSource.readString(CONTACT_USER_ID.value)
            val firebaseUserId = storageDataSource.readString(FIREBASE_USER_ID.value)
            val firebaseToken = storageDataSource.readString(FIREBASE_TOKEN.value)
            val contactPrimaryMobile = try {
                storageDataSource.readString(CONTACT_PRIMARY_MOBILE.value)
                    ?.addCountryCodeIfMissing()
            } catch (e: Exception) {
                null
            }
            RetrieveCredentialsSuccess.FromStorage(
                InterAppCredentials(
                    contactUserId = contactUserId!!,
                    firebaseUserId = firebaseUserId!!,
                    firebaseToken = firebaseToken!!,
                    contactPrimaryMobile = contactPrimaryMobile
                )
            )
        } catch (e: Exception) {
            RetrieveCredentialsError.NoCredentialsFound
        }
    }
}