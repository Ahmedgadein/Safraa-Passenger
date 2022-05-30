package com.dinder.rihla.rider.ui.ticket_detail // ktlint-disable experimental:package-name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinder.rihla.rider.adapter.StringItemsAdapter
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.databinding.TicketDetailFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TicketDetailFragment : RihlaFragment() {
    private val viewModel: TicketDetailViewModel by viewModels()
    private val args: TicketDetailFragmentArgs by navArgs()
    private lateinit var binding: TicketDetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TicketDetailFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        val seatAdapter = StringItemsAdapter()

        binding.seatsRecyclerView.apply {
            adapter = seatAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getTrip(args.ticketId)
                viewModel.state.collect {
                    binding.ticketDetailProgressBar.isVisible = it.loading

                    it.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                    }

                    it.ticket?.let { ticket ->
                        binding.ticket = ticket
                        seatAdapter.submitList(ticket.seats)
                    }
                }
            }
        }
    }
}
