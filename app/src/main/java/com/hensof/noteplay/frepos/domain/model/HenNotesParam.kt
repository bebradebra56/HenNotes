package com.hensof.noteplay.frepos.domain.model

import com.google.gson.annotations.SerializedName


private const val HEN_NOTES_A = "com.hensof.noteplay"
private const val HEN_NOTES_B = "hen-notes"
data class HenNotesParam (
    @SerializedName("af_id")
    val henNotesAfId: String,
    @SerializedName("bundle_id")
    val henNotesBundleId: String = HEN_NOTES_A,
    @SerializedName("os")
    val henNotesOs: String = "Android",
    @SerializedName("store_id")
    val henNotesStoreId: String = HEN_NOTES_A,
    @SerializedName("locale")
    val henNotesLocale: String,
    @SerializedName("push_token")
    val henNotesPushToken: String,
    @SerializedName("firebase_project_id")
    val henNotesFirebaseProjectId: String = HEN_NOTES_B,

    )