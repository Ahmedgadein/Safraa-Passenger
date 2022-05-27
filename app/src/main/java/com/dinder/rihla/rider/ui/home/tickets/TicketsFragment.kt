package com.dinder.rihla.rider.ui.home.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinder.rihla.rider.adapter.TicketAdapter
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.databinding.TicketsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TicketsFragment : RihlaFragment() {
    private val viewModel: TicketsViewModel by viewModels()
    private lateinit var binding: TicketsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TicketsFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        val ticketAdapter = TicketAdapter()
        binding.ticketsRecyclerView.apply {
            adapter = ticketAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    binding.ticketProgressBar.isVisible = it.loading

                    it.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                    }

                    ticketAdapter.submitList(it.tickets)
                }
            }
        }
    }
}
