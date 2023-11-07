package com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource.converters.fillWithInterAppCredentials
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentials
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.CONTACT_USER_ID
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.FIREBASE_TOKEN
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.FIREBASE_USER_ID
import com.lumilabs.sunshine.sharedlibraries.interappcommunication.model.InterAppCredentialsKeys.PHONE_NUMBER_OR_FIREBASE_USER_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

open class InterAppContentProvider : ContentProvider() {

    open suspend fun provideInterAppCredentials(): InterAppCredentials? {
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {
        return when (matchWithPackageUri(uri)) {
            1 -> {
                var cursor: MatrixCursor? = MatrixCursor(
                    arrayOf(
                        CONTACT_USER_ID.value,
                        FIREBASE_USER_ID.value,
                        FIREBASE_TOKEN.value,
                        PHONE_NUMBER_OR_FIREBASE_USER_ID.value
                    )
                )

                cursor!!.setNotificationUri(context?.contentResolver, uri)

                runBlocking(Dispatchers.IO) {
                    provideInterAppCredentials()?.let { credentials ->
                        cursor!!.fillWithInterAppCredentials(credentials)
                    } ?: run {
                        cursor = null
                    }
                }

                cursor
            }

            else -> null
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        return 0
    }

    private fun matchWithPackageUri(uri: Uri): Int {
        val packageName = context?.packageName ?: return 0
        val authority = "$packageName.InterAppContentProvider"
        return UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(authority, "access", 1)
        }.match(uri)
    }
}