package com.hensof.noteplay.frepos

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.hensof.noteplay.frepos.presentation.app.HenNotesApplication

class HenNotesGlobalLayoutUtil {

    private var henNotesMChildOfContent: View? = null
    private var henNotesUsableHeightPrevious = 0

    fun henNotesAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        henNotesMChildOfContent = content.getChildAt(0)

        henNotesMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val henNotesUsableHeightNow = henNotesComputeUsableHeight()
        if (henNotesUsableHeightNow != henNotesUsableHeightPrevious) {
            val henNotesUsableHeightSansKeyboard = henNotesMChildOfContent?.rootView?.height ?: 0
            val henNotesHeightDifference = henNotesUsableHeightSansKeyboard - henNotesUsableHeightNow

            if (henNotesHeightDifference > (henNotesUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(HenNotesApplication.henNotesInputMode)
            } else {
                activity.window.setSoftInputMode(HenNotesApplication.henNotesInputMode)
            }
//            mChildOfContent?.requestLayout()
            henNotesUsableHeightPrevious = henNotesUsableHeightNow
        }
    }

    private fun henNotesComputeUsableHeight(): Int {
        val r = Rect()
        henNotesMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}