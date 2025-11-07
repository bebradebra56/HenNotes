package com.hensof.noteplay.frepos.presentation.di

import com.hensof.noteplay.frepos.data.repo.HenNotesRepository
import com.hensof.noteplay.frepos.data.shar.HenNotesSharedPreference
import com.hensof.noteplay.frepos.data.utils.HenNotesPushToken
import com.hensof.noteplay.frepos.data.utils.HenNotesSystemService
import com.hensof.noteplay.frepos.domain.usecases.HenNotesGetAllUseCase
import com.hensof.noteplay.frepos.presentation.pushhandler.HenNotesPushHandler
import com.hensof.noteplay.frepos.presentation.ui.load.HenNotesLoadViewModel
import com.hensof.noteplay.frepos.presentation.ui.view.HenNotesViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val henNotesModule = module {
    factory {
        HenNotesPushHandler()
    }
    single {
        HenNotesRepository()
    }
    single {
        HenNotesSharedPreference(get())
    }
    factory {
        HenNotesPushToken()
    }
    factory {
        HenNotesSystemService(get())
    }
    factory {
        HenNotesGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        HenNotesViFun(get())
    }
    viewModel {
        HenNotesLoadViewModel(get(), get(), get())
    }
}