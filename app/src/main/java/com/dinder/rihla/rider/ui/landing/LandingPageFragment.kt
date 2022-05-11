package com.dinder.rihla.rider.ui.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.common.RihlaFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LandingPageFragment : RihlaFragment() {
    private val viewModel: LandingPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUI()
        return inflater.inflate(R.layout.landing_page_fragment, container, false)
    }

    private fun setUI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    if (it.navigateToHome) {
                        navigateToHome()
                        return@collect
                    }

                    if (it.navigateToLogin) {
                        navigateToLogin()
                        return@collect
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(
            LandingPageFragmentDirections.actionLandingPageFragmentToHomeFragment()
        )
    }

    private fun navigateToLogin() {
        findNavController().navigate(
            LandingPageFragmentDirections.actionLandingPageFragmentToLoginFragment()
        )
    }
}
