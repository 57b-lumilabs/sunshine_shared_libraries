package com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result

sealed class DeleteCredentialsResult {

    data object DeleteSuccess : DeleteCredentialsResult()

    data class DeleteError(val exception: Exception?) : DeleteCredentialsResult()
}