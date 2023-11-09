package com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result

sealed class SaveCredentialsResult {

    data object SaveSuccess : SaveCredentialsResult()

    data class Error(val exception: Exception) : SaveCredentialsResult()
}