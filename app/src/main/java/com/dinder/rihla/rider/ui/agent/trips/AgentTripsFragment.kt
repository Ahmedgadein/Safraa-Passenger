package com.dinder.rihla.rider.ui.agent.trips

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.adapter.AgentTripAdapter
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.databinding.AgentTripsFragmentBinding
import com.dinder.rihla.rider.ui.agent.home.AgentHomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AgentTripsFragment : RihlaFragment() {

    private val viewModel: AgentTripsViewModel by viewModels()
    private lateinit var binding: AgentTripsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = AgentTripsFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        val tripsAdapter = AgentTripAdapter()
        binding.tripsRecyclerView.apply {
            adapter = tripsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.settingsButton.setOnClickListener {
            findNavController().navigate(
                AgentHomeFragmentDirections.actionAgentHomeFragmentToSettingsFragment()
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    state.messages.firstOrNull()?.let {
                        showSnackbar(it.content)
                        viewModel.userMessageShown(it.id)
                    }
                    setPromoCode(state)
                    setProgressBar(state)
                    setTripsList(state, tripsAdapter)
                    setCopyButton(state)
                    setShareButton(state)
                }
            }
        }
    }

    private fun setShareButton(state: AgentTripsUiState) {
        binding.shareButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, state.user?.id)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun setCopyButton(state: AgentTripsUiState) {
        val clipBoard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        binding.copyButton.setOnClickListener {
            val clipData = ClipData.newPlainText(
                resources.getString(R.string.promo_code),
                state.user?.id
            )
            clipBoard.setPrimaryClip(clipData)
            showSnackbar(resources.getString(R.string.copied))
        }
    }

    private fun setPromoCode(state: AgentTripsUiState) {
        state.user?.let {
            binding.code = it.id
        }
    }

    private fun setTripsList(state: AgentTripsUiState, adapter: AgentTripAdapter) {
        adapter.submitList(state.trips)
    }

    private fun setProgressBar(state: AgentTripsUiState) {
        binding.tripsProgressBar.isVisible = state.loading && state.trips.isEmpty()
        binding.noTripsFound.isVisible = !state.loading && state.trips.isEmpty()
    }
}
