package com.hensof.noteplay.frepos.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hensof.noteplay.MainActivity
import com.hensof.noteplay.R
import com.hensof.noteplay.databinding.FragmentLoadHenNotesBinding
import com.hensof.noteplay.frepos.data.shar.HenNotesSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HenNotesLoadFragment : Fragment(R.layout.fragment_load_hen_notes) {
    private lateinit var henNotesLoadBinding: FragmentLoadHenNotesBinding

    private val henNotesLoadViewModel by viewModel<HenNotesLoadViewModel>()

    private val henNotesSharedPreference by inject<HenNotesSharedPreference>()

    private var henNotesUrl = ""

    private val henNotesRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            henNotesNavigateToSuccess(henNotesUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                henNotesSharedPreference.henNotesNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                henNotesNavigateToSuccess(henNotesUrl)
            } else {
                henNotesNavigateToSuccess(henNotesUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        henNotesLoadBinding = FragmentLoadHenNotesBinding.bind(view)

        henNotesLoadBinding.henNotesGrandButton.setOnClickListener {
            val henNotesPermission = Manifest.permission.POST_NOTIFICATIONS
            henNotesRequestNotificationPermission.launch(henNotesPermission)
            henNotesSharedPreference.henNotesNotificationRequestedBefore = true
        }

        henNotesLoadBinding.henNotesSkipButton.setOnClickListener {
            henNotesSharedPreference.henNotesNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            henNotesNavigateToSuccess(henNotesUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                henNotesLoadViewModel.henNotesHomeScreenState.collect {
                    when (it) {
                        is HenNotesLoadViewModel.HenNotesHomeScreenState.HenNotesLoading -> {

                        }

                        is HenNotesLoadViewModel.HenNotesHomeScreenState.HenNotesError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is HenNotesLoadViewModel.HenNotesHomeScreenState.HenNotesSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val henNotesPermission = Manifest.permission.POST_NOTIFICATIONS
                                val henNotesPermissionRequestedBefore = henNotesSharedPreference.henNotesNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), henNotesPermission) == PackageManager.PERMISSION_GRANTED) {
                                    henNotesNavigateToSuccess(it.data)
                                } else if (!henNotesPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > henNotesSharedPreference.henNotesNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    henNotesLoadBinding.henNotesNotiGroup.visibility = View.VISIBLE
                                    henNotesLoadBinding.henNotesLoadingGroup.visibility = View.GONE
                                    henNotesUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(henNotesPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > henNotesSharedPreference.henNotesNotificationRequest) {
                                        henNotesLoadBinding.henNotesNotiGroup.visibility = View.VISIBLE
                                        henNotesLoadBinding.henNotesLoadingGroup.visibility = View.GONE
                                        henNotesUrl = it.data
                                    } else {
                                        henNotesNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    henNotesNavigateToSuccess(it.data)
                                }
                            } else {
                                henNotesNavigateToSuccess(it.data)
                            }
                        }

                        HenNotesLoadViewModel.HenNotesHomeScreenState.HenNotesNotInternet -> {
                            henNotesLoadBinding.henNotesStateGroup.visibility = View.VISIBLE
                            henNotesLoadBinding.henNotesLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun henNotesNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_henNotesLoadFragment_to_henNotesV,
            bundleOf(HEN_NOTES_D to data)
        )
    }

    companion object {
        const val HEN_NOTES_D = "henNotesData"
    }
}