package com.lumilabs.sunshine.sharedlibraries.interappcommunication.infrastructure


import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result.DeleteCredentialsResult
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result.RetrieveCredentialsResult
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result.SaveCredentialsResult

interface InterAppCredentialsRepository {
    suspend fun retrieveInterAppCredentials(
        packageNames: List<String>
    ): RetrieveCredentialsResult

    suspend fun saveInterAppCredentials(
        contactUserId: String?,
        contactPrimaryMobile: String?
    ): SaveCredentialsResult

    suspend fun clearInterAppCredentials(): DeleteCredentialsResult
}