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
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.data.model.Role
import com.dinder.rihla.rider.data.model.User
import com.dinder.rihla.rider.databinding.LandingPageFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LandingPageFragment : RihlaFragment() {
    private val viewModel: LandingPageViewModel by viewModels()
    private lateinit var binding: LandingPageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = LandingPageFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    it.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                    }

                    if (it.navigateToUpdate) {
                        navigateToUpdate()
                        return@collect
                    }
                    if (it.navigateToHome) {
                        navigateToHome(it.user)
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

    private fun navigateToHome(user: User?) {
        user?.let {
            val direction = if (user.role == Role.PASSENGER)
                LandingPageFragmentDirections.actionLandingPageFragmentToHomeFragment()
            else LandingPageFragmentDirections.actionLandingPageFragmentToAgentHomeFragment()
            findNavController().navigate(direction)
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(
            LandingPageFragmentDirections.actionLandingPageFragmentToLoginFragment()
        )
    }

    private fun navigateToUpdate() {
        findNavController().navigate(
            LandingPageFragmentDirections.actionLandingPageFragmentToUpdateAppFragment()
        )
    }
}
