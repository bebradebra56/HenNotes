package com.hensof.noteplay.frepos.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hensof.noteplay.frepos.presentation.app.HenNotesApplication
import com.hensof.noteplay.frepos.presentation.ui.load.HenNotesLoadFragment
import org.koin.android.ext.android.inject

class HenNotesV : Fragment(){

    private lateinit var henNotesPhoto: Uri
    private var henNotesFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val henNotesTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        henNotesFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        henNotesFilePathFromChrome = null
    }

    private val henNotesTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            henNotesFilePathFromChrome?.onReceiveValue(arrayOf(henNotesPhoto))
            henNotesFilePathFromChrome = null
        } else {
            henNotesFilePathFromChrome?.onReceiveValue(null)
            henNotesFilePathFromChrome = null
        }
    }

    private val henNotesDataStore by activityViewModels<HenNotesDataStore>()


    private val henNotesViFun by inject<HenNotesViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (henNotesDataStore.henNotesView.canGoBack()) {
                        henNotesDataStore.henNotesView.goBack()
                        Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "WebView can go back")
                    } else if (henNotesDataStore.henNotesViList.size > 1) {
                        Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "WebView can`t go back")
                        henNotesDataStore.henNotesViList.removeAt(henNotesDataStore.henNotesViList.lastIndex)
                        Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "WebView list size ${henNotesDataStore.henNotesViList.size}")
                        henNotesDataStore.henNotesView.destroy()
                        val previousWebView = henNotesDataStore.henNotesViList.last()
                        henNotesAttachWebViewToContainer(previousWebView)
                        henNotesDataStore.henNotesView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (henNotesDataStore.henNotesIsFirstCreate) {
            henNotesDataStore.henNotesIsFirstCreate = false
            henNotesDataStore.henNotesContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return henNotesDataStore.henNotesContainerView
        } else {
            return henNotesDataStore.henNotesContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "onViewCreated")
        if (henNotesDataStore.henNotesViList.isEmpty()) {
            henNotesDataStore.henNotesView = HenNotesVi(requireContext(), object :
                HenNotesCallBack {
                override fun henNotesHandleCreateWebWindowRequest(henNotesVi: HenNotesVi) {
                    henNotesDataStore.henNotesViList.add(henNotesVi)
                    Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "WebView list size = ${henNotesDataStore.henNotesViList.size}")
                    Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "CreateWebWindowRequest")
                    henNotesDataStore.henNotesView = henNotesVi
                    henNotesVi.henNotesSetFileChooserHandler { callback ->
                        henNotesHandleFileChooser(callback)
                    }
                    henNotesAttachWebViewToContainer(henNotesVi)
                }

            }, henNotesWindow = requireActivity().window).apply {
                henNotesSetFileChooserHandler { callback ->
                    henNotesHandleFileChooser(callback)
                }
            }
            henNotesDataStore.henNotesView.henNotesFLoad(arguments?.getString(HenNotesLoadFragment.HEN_NOTES_D) ?: "")
//            ejvview.fLoad("www.google.com")
            henNotesDataStore.henNotesViList.add(henNotesDataStore.henNotesView)
            henNotesAttachWebViewToContainer(henNotesDataStore.henNotesView)
        } else {
            henNotesDataStore.henNotesViList.forEach { webView ->
                webView.henNotesSetFileChooserHandler { callback ->
                    henNotesHandleFileChooser(callback)
                }
            }
            henNotesDataStore.henNotesView = henNotesDataStore.henNotesViList.last()

            henNotesAttachWebViewToContainer(henNotesDataStore.henNotesView)
        }
        Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "WebView list size = ${henNotesDataStore.henNotesViList.size}")
    }

    private fun henNotesHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        henNotesFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Launching file picker")
                    henNotesTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Launching camera")
                    henNotesPhoto = henNotesViFun.henNotesSavePhoto()
                    henNotesTakePhoto.launch(henNotesPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                henNotesFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun henNotesAttachWebViewToContainer(w: HenNotesVi) {
        henNotesDataStore.henNotesContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            henNotesDataStore.henNotesContainerView.removeAllViews()
            henNotesDataStore.henNotesContainerView.addView(w)
        }
    }


}