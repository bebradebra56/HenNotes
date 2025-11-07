package com.hensof.noteplay.frepos.data.shar

import android.content.Context
import androidx.core.content.edit

class HenNotesSharedPreference(context: Context) {
    private val henNotesPrefs = context.getSharedPreferences("henNotesSharedPrefsAb", Context.MODE_PRIVATE)

    var henNotesSavedUrl: String
        get() = henNotesPrefs.getString(HEN_NOTES_SAVED_URL, "") ?: ""
        set(value) = henNotesPrefs.edit { putString(HEN_NOTES_SAVED_URL, value) }

    var henNotesExpired : Long
        get() = henNotesPrefs.getLong(HEN_NOTES_EXPIRED, 0L)
        set(value) = henNotesPrefs.edit { putLong(HEN_NOTES_EXPIRED, value) }

    var henNotesAppState: Int
        get() = henNotesPrefs.getInt(HEN_NOTES_APPLICATION_STATE, 0)
        set(value) = henNotesPrefs.edit { putInt(HEN_NOTES_APPLICATION_STATE, value) }

    var henNotesNotificationRequest: Long
        get() = henNotesPrefs.getLong(HEN_NOTES_NOTIFICAITON_REQUEST, 0L)
        set(value) = henNotesPrefs.edit { putLong(HEN_NOTES_NOTIFICAITON_REQUEST, value) }

    var henNotesNotificationRequestedBefore: Boolean
        get() = henNotesPrefs.getBoolean(HEN_NOTES_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = henNotesPrefs.edit { putBoolean(
            HEN_NOTES_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val HEN_NOTES_SAVED_URL = "henNotesSavedUrl"
        private const val HEN_NOTES_EXPIRED = "henNotesExpired"
        private const val HEN_NOTES_APPLICATION_STATE = "henNotesApplicationState"
        private const val HEN_NOTES_NOTIFICAITON_REQUEST = "henNotesNotificationRequest"
        private const val HEN_NOTES_NOTIFICATION_REQUEST_BEFORE = "henNotesNotificationRequestedBefore"
    }
}