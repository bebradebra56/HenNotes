package com.hensof.noteplay.frepos.domain.usecases

import android.util.Log
import com.hensof.noteplay.frepos.data.repo.HenNotesRepository
import com.hensof.noteplay.frepos.data.utils.HenNotesPushToken
import com.hensof.noteplay.frepos.data.utils.HenNotesSystemService
import com.hensof.noteplay.frepos.domain.model.HenNotesEntity
import com.hensof.noteplay.frepos.domain.model.HenNotesParam
import com.hensof.noteplay.frepos.presentation.app.HenNotesApplication

class HenNotesGetAllUseCase(
    private val henNotesRepository: HenNotesRepository,
    private val henNotesSystemService: HenNotesSystemService,
    private val henNotesPushToken: HenNotesPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : HenNotesEntity?{
        val params = HenNotesParam(
            henNotesLocale = henNotesSystemService.henNotesGetLocale(),
            henNotesPushToken = henNotesPushToken.henNotesGetToken(),
            henNotesAfId = henNotesSystemService.henNotesGetAppsflyerId()
        )
        Log.d(HenNotesApplication.HEN_NOTES_MAIN_TAG, "Params for request: $params")
        return henNotesRepository.henNotesGetClient(params, conversion)
    }



}