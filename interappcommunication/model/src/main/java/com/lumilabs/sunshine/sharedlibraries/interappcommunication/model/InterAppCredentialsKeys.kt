package com.lumilabs.sunshine.sharedlibraries.interappcommunication.model

enum class InterAppCredentialsKeys(val value: String) {
    CONTACT_USER_ID("contactUserId"),
    FIREBASE_USER_ID("firebaseUserId"),
    FIREBASE_TOKEN("firebaseToken"),
    PHONE_NUMBER_OR_FIREBASE_USER_ID("phoneNumberOrFirebaseUserId"),
}