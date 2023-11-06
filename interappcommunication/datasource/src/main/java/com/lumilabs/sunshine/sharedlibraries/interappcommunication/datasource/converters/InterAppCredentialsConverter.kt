package com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource.converters

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.MatrixCursor

import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentials
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.*

fun MatrixCursor.fillWithInterAppCredentials(credentials: InterAppCredentials) {
    this.newRow()
        .add(credentials.contactUserId)
        .add(credentials.firebaseUserId)
        .add(credentials.firebaseToken)
        .add(credentials.phoneNumberOrFirebaseUserId)
}

@SuppressLint("Range")
fun Cursor?.convertToInterAppCredentials(): InterAppCredentials? {
    if (this == null) return null
    return try {
        if (this.moveToFirst()) {
            val contactUserId = this.getString(this.getColumnIndex(CONTACT_USER_ID.value))
            val firebaseUserId = this.getString(this.getColumnIndex(FIREBASE_USER_ID.value))
            val firebaseToken = this.getString(this.getColumnIndex(FIREBASE_TOKEN.value))
            val phoneNumberOrFirebaseUserId =
                this.getString(this.getColumnIndex(PHONE_NUMBER_OR_FIREBASE_USER_ID.value))

            InterAppCredentials(
                contactUserId = contactUserId,
                firebaseUserId = firebaseUserId,
                firebaseToken = firebaseToken,
                phoneNumberOrFirebaseUserId = phoneNumberOrFirebaseUserId
            )
        } else null
    } catch (e: Exception) {
        null
    }
}