package com.lumilabs.sunshine.sharedlibraries.interappcommunication.model

data class InterAppCredentials(
    val contactUserId: String,
    val firebaseUserId: String,
    val firebaseToken: String,
    val phoneNumberOrFirebaseUserId: String
)