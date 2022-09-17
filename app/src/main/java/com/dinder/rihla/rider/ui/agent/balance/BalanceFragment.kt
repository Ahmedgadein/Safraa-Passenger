package com.dinder.rihla.rider.ui.agent.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.databinding.BalanceFragmentBinding
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BalanceFragment : RihlaFragment() {
    private val viewModel: BalanceViewModel by viewModels()
    private lateinit var binding: BalanceFragmentBinding

    @Inject
    lateinit var mixpanel: MixpanelAPI

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BalanceFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mixpanel.track("Agent View Balance")
    }

    private fun setUI() {
        binding.withdrawButton.setOnClickListener {
            // TODO: Redirect to Whatsapp
            mixpanel.track("Agent Withdraw Balance")
        }

        binding.retryButton.setOnClickListener {
            viewModel.retry()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getWalletBalance()
                viewModel.state.collect { state ->

                    state.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                    }

                    if (state.loading) {
                        binding.loading.isVisible = true
                        binding.error.isVisible = false
                        return@collect
                    }

                    if (state.error) {
                        binding.loading.isVisible = false
                        binding.error.isVisible = true
                        return@collect
                    }

                    if (!state.loading && !state.error) {

                        binding.loading.isVisible = false
                        binding.error.isVisible = false
                        binding.balance.text = state.balance.toString()
                    }
                }
            }
        }
    }
}
