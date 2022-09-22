package com.dinder.rihla.rider.ui.trip_detail // ktlint-disable experimental:package-name

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.adapter.StringItemsAdapter
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.data.model.Seat
import com.dinder.rihla.rider.databinding.PrebookSuccessfulDialogBinding
import com.dinder.rihla.rider.databinding.TripDetailFragmentBinding
import com.dinder.rihla.rider.ui.home.HomeFragmentDirections
import com.dinder.rihla.rider.utils.NetworkUtils
import com.dinder.rihla.rider.utils.SeatUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
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
        savedInstanceState: Bundle?,
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
            showConfirmationBottomSheet(seats)
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
                        val props = JSONObject().apply {
                            put("From", it.trip?.from?.name)
                            put("To", it.trip?.to?.name)
                            put("Price", it.trip?.price)
                            put("Departure", it.trip?.departure)
                            put("Trip ID:", args.tripID)
                        }

                        mixpanel.track("Reservation Successful", props)
                        showSuccessfulPrebookingBottomSheet(it.ticketID)
                    }
                }
            }
        }
    }

    private fun showSuccessfulPrebookingBottomSheet(ticketId: String) {
        val binding = PrebookSuccessfulDialogBinding.inflate(layoutInflater, null, false)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(false)
            .show()
        dialog.setCanceledOnTouchOutside(false)
        binding.payNowButton.setOnClickListener {
            dialog.dismiss()
            findNavController().navigateUp()
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToPaymentFragment(
                    ticketId
                )
            )
        }

        binding.payLaterButton.setOnClickListener {
            dialog.dismiss()
            findNavController().navigateUp()
        }
    }

    private fun showConfirmationBottomSheet(seats: List<Seat>) {
        // Initialize Bottomsheet Dialog
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.confirm_seats_bottomsheet_dialog)

        // Seats RecyclerView
        val seatAdapter = StringItemsAdapter()
        bottomSheetDialog.findViewById<RecyclerView>(R.id.confirmSeatsRecyclerView)?.apply {
            adapter = seatAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        // Submit seats as String list
        val result = seats.map { it.number.toString() }
        seatAdapter.submitList(result)

        bottomSheetDialog.findViewById<Button>(R.id.confirmSeatsButton)?.setOnClickListener {
            if (!NetworkUtils.isNetworkConnected(requireContext())) {
                showToast(resources.getString(R.string.no_network))
                return@setOnClickListener
            }

            // Track event
            val props = JSONObject().apply {
                put("Seats: ", result)
                put("Seats count: ", result.size)
                put("Trip ID: ", args.tripID)
            }
            mixpanel.track("Reservation Attempt", props)
            viewModel.reserveSeats(args.tripID, seats)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }
}
