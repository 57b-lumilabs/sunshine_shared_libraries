package com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result

import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentials

abstract class RetrieveCredentialsResult

sealed class RetrieveCredentialsSuccess: RetrieveCredentialsResult() {
    data class FromStorage(val data: InterAppCredentials) : RetrieveCredentialsSuccess()
    data class FromContentResolver(val data: InterAppCredentials) : RetrieveCredentialsSuccess()
}

sealed class RetrieveCredentialsError: RetrieveCredentialsResult() {
    data class ContentResolverConversion(val exception: Exception) : RetrieveCredentialsError()
    data class ContentResolverPermission(val exception: Exception) : RetrieveCredentialsError()
    data class MultipleErrors(val errors: Map<String, RetrieveCredentialsError>): RetrieveCredentialsError()
    data object NoCredentialsFound: RetrieveCredentialsSuccess()
}