package com.hensof.noteplay.frepos.domain.model

import com.google.gson.annotations.SerializedName


data class HenNotesEntity (
    @SerializedName("ok")
    val henNotesOk: String,
    @SerializedName("url")
    val henNotesUrl: String,
    @SerializedName("expires")
    val henNotesExpires: Long,
)