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
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class TripsFragment : RihlaFragment() {

    private val viewModel: TripsViewModel by viewModels()
    private lateinit var binding: TripsFragmentBinding

    @Inject
    lateinit var mixpanel: MixpanelAPI

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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

        binding.clearDestinationsButton.setOnClickListener {
            binding.fromDropdown.text.clear()
            binding.toDropdown.text.clear()
            binding.toDropdown.clearFocus()
            binding.fromDropdown.clearFocus()
            viewModel.clearDestinations()
            mixpanel.track("Clear Destinations")
        }

        val tripsAdapter = TripAdapter()
        binding.tripsRecyclerView.apply {
            adapter = tripsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.tripsProgressBar.isVisible = state.loading
                    binding.noTripsFound.isVisible = !state.loading && state.trips.isNullOrEmpty()

                    state.messages.firstOrNull()?.let {
                        showSnackbar(it.content)
                        viewModel.userMessageShown(it.id)
                    }

                    tripsAdapter.submitList(state.trips)

                    val from = state.destinations.filterNot { it == state.toDestination }
                    setFromDestinations(from)

                    val to = state.destinations.filterNot { it == state.fromDestination }
                    setToDestinations(to)
                }
            }
        }
    }

    private fun setFromDestinations(destinations: List<Destination>) {
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
            val props = JSONObject().apply {
                put("Selected Destination", location.name)
            }
            mixpanel.track("From Destination Change", props)
        }
    }

    private fun setToDestinations(destinations: List<Destination>) {
        val adapter = DestinationAdapter(
            requireContext(),
            R.layout.destination_item,
            destinations.toMutableList()
        )

        binding.toDropdown.setAdapter(adapter)
        binding.toDropdown.setOnItemClickListener { _, _, position, _ ->
            val location = adapter.getItem(
                position
            )
            viewModel.onDestinationUpdate(to = location)
            val props = JSONObject().apply {
                put("Selected Destination", location.name)
            }
            mixpanel.track("To Destination Change", props)
        }
    }
}
