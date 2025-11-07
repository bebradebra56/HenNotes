package com.hensof.noteplay

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.hensof.noteplay.frepos.presentation.app.HenNotesApplication
import com.hensof.noteplay.frepos.presentation.pushhandler.HenNotesPushHandler
import com.hensof.noteplay.frepos.HenNotesGlobalLayoutUtil
import com.hensof.noteplay.frepos.henNotesSetupSystemBars
import org.koin.android.ext.android.inject

class HenNotesActivity : AppCompatActivity() {

    private val henNotesPushHandler by inject<HenNotesPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        henNotesSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_hen_notes)

        val henNotesRootView = findViewById<View>(android.R.id.content)
        HenNotesGlobalLayoutUtil().henNotesAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(henNotesRootView) { henNotesView, henNotesInsets ->
            val henNotesSystemBars = henNotesInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val henNotesDisplayCutout = henNotesInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val henNotesIme = henNotesInsets.getInsets(WindowInsetsCompat.Type.ime())


            val henNotesTopPadding = maxOf(henNotesSystemBars.top, henNotesDisplayCutout.top)
            val henNotesLeftPadding = maxOf(henNotesSystemBars.left, henNotesDisplayCutout.left)
            val henNotesRightPadding = maxOf(henNotesSystemBars.right, henNotesDisplayCutout.right)
            window.setSoftInputMode(HenNotesApplication.henNotesInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "ADJUST PUN")
                val henNotesBottomInset = maxOf(henNotesSystemBars.bottom, henNotesDisplayCutout.bottom)

                henNotesView.setPadding(henNotesLeftPadding, henNotesTopPadding, henNotesRightPadding, 0)

                henNotesView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = henNotesBottomInset
                }
            } else {
                Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "ADJUST RESIZE")

                val henNotesBottomInset = maxOf(henNotesSystemBars.bottom, henNotesDisplayCutout.bottom, henNotesIme.bottom)

                henNotesView.setPadding(henNotesLeftPadding, henNotesTopPadding, henNotesRightPadding, 0)

                henNotesView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = henNotesBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Activity onCreate()")
        henNotesPushHandler.henNotesHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            henNotesSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        henNotesSetupSystemBars()
    }
}