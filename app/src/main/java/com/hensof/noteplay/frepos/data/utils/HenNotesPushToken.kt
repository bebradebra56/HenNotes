package com.hensof.noteplay.frepos.data.utils

import android.util.Log
import com.hensof.noteplay.frepos.presentation.app.HenNotesApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HenNotesPushToken {

    suspend fun henNotesGetToken(
        henNotesTrackerMaxAttempts: Int = 3,
        henNotesTrackerDelayMs: Long = 1500
    ): String {

        repeat(henNotesTrackerMaxAttempts - 1) {
            try {
                val henNotesTrackerToken = FirebaseMessaging.getInstance().token.await()
                return henNotesTrackerToken
            } catch (e: Exception) {
                Log.e(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(henNotesTrackerDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}