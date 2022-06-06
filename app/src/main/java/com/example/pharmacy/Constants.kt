package com.example.pharmacy

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constants {
    const val USERS: String = "users"
    const val DRUGS: String = "drugs"
    const val PREF: String = "prefs"
    const val LOG_USER: String = "logusername"
    const val USER_DETAILS: String = "user_details"
    const val MALE: String = "Male"
    const val FEMALE: String = "Female"
    const val OTHER: String = "Other"

    const val MOBILE: String = "mobile"
    const val SEX: String = "sex"
    const val IMAGE: String = "image"

    const val USER_ID: String = "user_id"

    const val PROFILE_IMAGE: String = "profile_image"
    const val COMPLETE_PROFILE: String = "profileCompleted"

    const val DRUG_IMAGE: String = "drug_image"

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}