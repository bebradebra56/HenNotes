package com.hensof.noteplay.frepos.data.utils

import android.util.Log
import com.hensof.noteplay.frepos.presentation.app.HenNotesApplication
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HenNotesPushToken {

    suspend fun henNotesGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}