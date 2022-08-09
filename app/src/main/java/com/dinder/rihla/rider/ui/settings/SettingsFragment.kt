package com.dinder.rihla.rider.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.databinding.SettingsFragmentBinding
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : RihlaFragment() {
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var binding: SettingsFragmentBinding
    @Inject
    lateinit var mixpanel: MixpanelAPI

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            mixpanel.track("Logout")
            findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToLandingPageFragment()
            )
        }
    }
}
