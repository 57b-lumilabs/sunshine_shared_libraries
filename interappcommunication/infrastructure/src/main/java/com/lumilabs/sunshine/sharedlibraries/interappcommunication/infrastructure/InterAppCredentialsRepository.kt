package com.lumilabs.sunshine.sharedlibraries.interappcommunication.infrastructure


import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentials

interface InterAppCredentialsRepository {
    suspend fun retrieveInterAppCredentials(): InterAppCredentials?

    suspend fun saveInterAppCredentials(
        contactUserId: String?,
        contactPrimaryMobile: String?
    )
}