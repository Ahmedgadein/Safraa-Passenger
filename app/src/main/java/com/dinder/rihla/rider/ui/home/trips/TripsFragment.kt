package com.dinder.rihla.rider.ui.home.trips

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
import com.dinder.rihla.rider.adapter.DestinationAdapter
import com.dinder.rihla.rider.adapter.TripAdapter
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.data.model.Destination
import com.dinder.rihla.rider.databinding.TripsFragmentBinding
import com.dinder.rihla.rider.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TripsFragment : RihlaFragment() {

    private val viewModel: TripsViewModel by viewModels()
    private lateinit var binding: TripsFragmentBinding

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TripsFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
            )
        }

        val tripsAdapter = TripAdapter()
        binding.tripsRecyclerView.apply {
            adapter = tripsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    binding.tripsProgressBar.isVisible = it.loading
                    binding.noTripsFound.isVisible = !it.loading && it.trips.isNullOrEmpty()

                    it.messages.firstOrNull()?.let {
                        showSnackbar(it.content)
                        viewModel.userMessageShown(it.id)
                    }

                    tripsAdapter.submitList(it.trips)
                    setDestinations(it.destinations)
                }
            }
        }
    }

    private fun setDestinations(destinations: List<Destination>) {
        val adapter = DestinationAdapter(
            requireContext(),
            R.layout.destination_item,
            destinations.toMutableList()
        )

        binding.fromDropdown.setAdapter(adapter)
        binding.fromDropdown.setOnItemClickListener { _, _, position, _ ->
            val location = adapter.getItem(
                position
            )
            viewModel.onDestinationUpdate(from = location)
        }

        binding.toDropdown.setAdapter(adapter)
        binding.toDropdown.setOnItemClickListener { _, _, position, _ ->
            val location = adapter.getItem(
                position
            )
            viewModel.onDestinationUpdate(to = location)
        }
    }
}
