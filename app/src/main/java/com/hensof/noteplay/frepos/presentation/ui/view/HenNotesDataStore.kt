package com.hensof.noteplay.frepos.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class HenNotesDataStore : ViewModel(){
    val henNotesViList: MutableList<HenNotesVi> = mutableListOf()
    var henNotesIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var henNotesContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var henNotesView: HenNotesVi

}