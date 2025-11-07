package com.hensof.noteplay.frepos.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.hensof.noteplay.frepos.presentation.app.HenNotesApplication

class HenNotesPushHandler {
    fun henNotesHandlePush(extras: Bundle?) {
        Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = henNotesBundleToMap(extras)
            Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    HenNotesApplication.HEN_NOTES_FB_LI = map["url"]
                    Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Push data no!")
        }
    }

    private fun henNotesBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}