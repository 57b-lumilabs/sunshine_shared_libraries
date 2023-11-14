package com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource.converters

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.MatrixCursor
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource.formatters.addCountryCodeIfMissing
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentials
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.*
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result.RetrieveCredentialsError
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result.RetrieveCredentialsResult
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.result.RetrieveCredentialsSuccess

fun MatrixCursor.fillWithInterAppCredentials(credentials: InterAppCredentials) {
    this.newRow()
        .add(credentials.contactUserId)
        .add(credentials.firebaseUserId)
        .add(credentials.firebaseToken)
        .add(credentials.contactPrimaryMobile)
}

@SuppressLint("Range")
fun Cursor?.convertToInterAppCredentials(): RetrieveCredentialsResult {
    if (this == null) return RetrieveCredentialsError.NoCredentialsFound
    return try {
        if (this.moveToFirst()) {
            val contactUserId = this.getString(this.getColumnIndex(CONTACT_USER_ID.value))
            val firebaseUserId = this.getString(this.getColumnIndex(FIREBASE_USER_ID.value))
            val firebaseToken = this.getString(this.getColumnIndex(FIREBASE_TOKEN.value))
            val contactPrimaryMobile = try {
                this.getString(this.getColumnIndex(CONTACT_PRIMARY_MOBILE.value))
                    .addCountryCodeIfMissing()
            } catch (e: Exception) {
                null
            }

            RetrieveCredentialsSuccess.FromContentResolver(
                InterAppCredentials(
                    contactUserId = contactUserId,
                    firebaseUserId = firebaseUserId,
                    firebaseToken = firebaseToken,
                    contactPrimaryMobile = contactPrimaryMobile
                )
            )
        } else RetrieveCredentialsError.NoCredentialsFound
    } catch (e: Exception) {
        RetrieveCredentialsError.ContentResolverConversion(e)
    }
}