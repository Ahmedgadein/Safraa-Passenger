package com.dinder.rihla.rider.ui.trip_detail // ktlint-disable experimental:package-name

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
import androidx.navigation.fragment.navArgs
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.databinding.TripDetailFragmentBinding
import com.dinder.rihla.rider.utils.SeatUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class TripDetailFragment : RihlaFragment() {
    private val viewModel: TripDetailViewModel by viewModels()
    private val args: TripDetailFragmentArgs by navArgs()
    private lateinit var binding: TripDetailFragmentBinding

    @Inject
    lateinit var mixpanel: MixpanelAPI

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TripDetailFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.tripDetailSeatView.setOnSeatSelectedListener { seats ->
            binding.seatCount.text = resources.getString(R.string.seats_selected, seats.size)
            binding.reserveButton.isEnabled = seats.isNotEmpty()
        }

        binding.reserveButton.setOnClickListener {
            val seats = SeatUtils.getSelectedSeats(binding.tripDetailSeatView.getSeats())
            val props = JSONObject().apply {
                put("Seats: ", seats.toString())
                put("Seats count: ", seats.size)
            }
            mixpanel.track("Reservation Attempt", props)
            viewModel.reserveSeats(args.tripID, seats)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getTrip(args.tripID)
                viewModel.state.collect {
                    binding.tripDetailProgressBar.isVisible = it.loading

                    it.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                    }

                    it.trip?.let { trip ->
                        binding.trip = trip
                        binding.tripDetailSeatView.setSeats(
                            SeatUtils.getSeatsListAsStateMap(trip.seats)
                        )

                        binding.tripDetailSeatView.setSeats(
                            SeatUtils.getSeatsListAsStateMap(trip.seats)
                        )
                    }

                    if (it.isReserved) {
                        showSnackbar(resources.getString(R.string.seats_reserved_successfully))
                        val props = JSONObject().apply {
                            put("From", it.trip?.from?.name)
                            put("To", it.trip?.to?.name)
                            put("Price", it.trip?.price)
                            put("Date", it.trip?.date)
                            put("Time", it.trip?.time)
                            put("Trip ID:", args.tripID)
                        }

                        mixpanel.track("Reservation Successful", props)
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }
}
