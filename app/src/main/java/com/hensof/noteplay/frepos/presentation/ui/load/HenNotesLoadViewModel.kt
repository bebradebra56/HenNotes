package com.hensof.noteplay.frepos.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hensof.noteplay.frepos.data.shar.HenNotesSharedPreference
import com.hensof.noteplay.frepos.data.utils.HenNotesSystemService
import com.hensof.noteplay.frepos.domain.usecases.HenNotesGetAllUseCase
import com.hensof.noteplay.frepos.presentation.app.HenNotesAppsFlyerState
import com.hensof.noteplay.frepos.presentation.app.HenNotesApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HenNotesLoadViewModel(
    private val henNotesGetAllUseCase: HenNotesGetAllUseCase,
    private val henNotesSharedPreference: HenNotesSharedPreference,
    private val henNotesSystemService: HenNotesSystemService
) : ViewModel() {

    private val _henNotesHomeScreenState: MutableStateFlow<HenNotesHomeScreenState> =
        MutableStateFlow(HenNotesHomeScreenState.HenNotesLoading)
    val henNotesHomeScreenState = _henNotesHomeScreenState.asStateFlow()

    private var henNotesGetApps = false


    init {
        viewModelScope.launch {
            when (henNotesSharedPreference.henNotesAppState) {
                0 -> {
                    if (henNotesSystemService.henNotesIsOnline()) {
                        HenNotesApplication.henNotesConversionFlow.collect {
                            when(it) {
                                HenNotesAppsFlyerState.HenNotesDefault -> {}
                                HenNotesAppsFlyerState.HenNotesError -> {
                                    henNotesSharedPreference.henNotesAppState = 2
                                    _henNotesHomeScreenState.value =
                                        HenNotesHomeScreenState.HenNotesError
                                    henNotesGetApps = true
                                }
                                is HenNotesAppsFlyerState.HenNotesSuccess -> {
                                    if (!henNotesGetApps) {
                                        henNotesGetData(it.henNotesData)
                                        henNotesGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _henNotesHomeScreenState.value =
                            HenNotesHomeScreenState.HenNotesNotInternet
                    }
                }
                1 -> {
                    if (henNotesSystemService.henNotesIsOnline()) {
                        if (HenNotesApplication.HEN_NOTES_FB_LI != null) {
                            _henNotesHomeScreenState.value =
                                HenNotesHomeScreenState.HenNotesSuccess(
                                    HenNotesApplication.HEN_NOTES_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > henNotesSharedPreference.henNotesExpired) {
                            Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Current time more then expired, repeat request")
                            HenNotesApplication.henNotesConversionFlow.collect {
                                when(it) {
                                    HenNotesAppsFlyerState.HenNotesDefault -> {}
                                    HenNotesAppsFlyerState.HenNotesError -> {
                                        _henNotesHomeScreenState.value =
                                            HenNotesHomeScreenState.HenNotesSuccess(
                                                henNotesSharedPreference.henNotesSavedUrl
                                            )
                                        henNotesGetApps = true
                                    }
                                    is HenNotesAppsFlyerState.HenNotesSuccess -> {
                                        if (!henNotesGetApps) {
                                            henNotesGetData(it.henNotesData)
                                            henNotesGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Current time less then expired, use saved url")
                            _henNotesHomeScreenState.value =
                                HenNotesHomeScreenState.HenNotesSuccess(
                                    henNotesSharedPreference.henNotesSavedUrl
                                )
                        }
                    } else {
                        _henNotesHomeScreenState.value =
                            HenNotesHomeScreenState.HenNotesNotInternet
                    }
                }
                2 -> {
                    _henNotesHomeScreenState.value =
                        HenNotesHomeScreenState.HenNotesError
                }
            }
        }
    }


    private suspend fun henNotesGetData(conversation: MutableMap<String, Any>?) {
        val henNotesData = henNotesGetAllUseCase.invoke(conversation)
        if (henNotesSharedPreference.henNotesAppState == 0) {
            if (henNotesData == null) {
                henNotesSharedPreference.henNotesAppState = 2
                _henNotesHomeScreenState.value =
                    HenNotesHomeScreenState.HenNotesError
            } else {
                henNotesSharedPreference.henNotesAppState = 1
                henNotesSharedPreference.apply {
                    henNotesExpired = henNotesData.henNotesExpires
                    henNotesSavedUrl = henNotesData.henNotesUrl
                }
                _henNotesHomeScreenState.value =
                    HenNotesHomeScreenState.HenNotesSuccess(henNotesData.henNotesUrl)
            }
        } else  {
            if (henNotesData == null) {
                _henNotesHomeScreenState.value =
                    HenNotesHomeScreenState.HenNotesSuccess(henNotesSharedPreference.henNotesSavedUrl)
            } else {
                henNotesSharedPreference.apply {
                    henNotesExpired = henNotesData.henNotesExpires
                    henNotesSavedUrl = henNotesData.henNotesUrl
                }
                _henNotesHomeScreenState.value =
                    HenNotesHomeScreenState.HenNotesSuccess(henNotesData.henNotesUrl)
            }
        }
    }


    sealed class HenNotesHomeScreenState {
        data object HenNotesLoading : HenNotesHomeScreenState()
        data object HenNotesError : HenNotesHomeScreenState()
        data class HenNotesSuccess(val data: String) : HenNotesHomeScreenState()
        data object HenNotesNotInternet: HenNotesHomeScreenState()
    }
}