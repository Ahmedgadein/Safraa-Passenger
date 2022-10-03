package com.dinder.rihla.rider.ui.ticket_detail // ktlint-disable experimental:package-name

import android.graphics.Paint
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.adapter.StringItemsAdapter
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.common.color
import com.dinder.rihla.rider.data.model.Ticket
import com.dinder.rihla.rider.data.model.TicketStatus
import com.dinder.rihla.rider.databinding.RedeemPromoCodeBottomsheetDialogBinding
import com.dinder.rihla.rider.databinding.TicketDetailFragmentBinding
import com.dinder.rihla.rider.utils.NetworkUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class TicketDetailFragment : RihlaFragment() {
    private val viewModel: TicketDetailViewModel by viewModels()
    private val args: TicketDetailFragmentArgs by navArgs()
    private lateinit var binding: TicketDetailFragmentBinding

    @Inject
    lateinit var mixpanel: MixpanelAPI

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
                viewModel.observeTicket(args.ticketId)
                viewModel.state.collect { state ->

                    state.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                    }

                    if (state.loading) {
                        binding.genericButton.hide()
                        binding.loading.isVisible = true
                        return@collect
                    }

                    if (!state.loading && state.ticket != null) {
                        binding.loading.isVisible = false
                        binding.ticket = state.ticket
                        setSeats(state.ticket)
                        setGenericButton(state)
                        setStatus(state)
                        setBasePrice(state.ticket)
                        setDiscountPrice(state.ticket)
                        setSeatsTimesPrice(state.ticket)
                        setPromoCode(state.ticket)
                        setPaymentDisprovedInfo(state.ticket)
                    }
                }
            }
        }
    }

    private fun setSeats(ticket: Ticket) {
        val seatAdapter = StringItemsAdapter()
        binding.seatsRecyclerView.apply {
            adapter = seatAdapter
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        seatAdapter.submitList(ticket.seats)
    }

    private fun setPaymentDisprovedInfo(ticket: Ticket) {
        binding.disprovedInfo.isVisible = (ticket.status == TicketStatus.DISPROVED)
    }

    private fun setPromoCode(ticket: Ticket) {
        if (!ticket.promoCode.isNullOrEmpty()) {
            binding.promoCodeButtonAndText.isVisible = false
            return
        }

        binding.usePromoCodeButton.setOnClickListener {
            showRedeemPromoCodeBottomSheet()
        }
    }

    private fun showRedeemPromoCodeBottomSheet() {
        val dialogBinding =
            RedeemPromoCodeBottomsheetDialogBinding.inflate(layoutInflater, null, false)

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        bottomSheetDialog.show()

        dialogBinding.redeemCodeButton.setOnClickListener {
            if (!NetworkUtils.isNetworkConnected(requireContext())) {
                bottomSheetDialog.dismiss()
                showSnackbar(resources.getString(R.string.no_network))
                return@setOnClickListener
            }

            val code = dialogBinding.promoCodeContainer.editText?.text.toString()
                .filter { !it.isWhitespace() }

            if (code.isEmpty()) {
                dialogBinding.promoCodeContainer.helperText = getString(R.string.required)
                return@setOnClickListener
            } else {
                dialogBinding.promoCodeContainer.helperText = null
                bottomSheetDialog.dismiss()
                viewModel.redeemCode(args.ticketId, code)
                val props = JSONObject().apply {
                    put("Code", code)
                }
                mixpanel.track("Redeem PromoCode Attempt", props)
            }
        }
    }

    private fun setSeatsTimesPrice(ticket: Ticket?) {
        ticket?.let {
            val count = ticket.seats.size
            val price = ((1 + ticket.rate) * ticket.price).roundToInt().toString()
            binding.seatsTimePrice.text =
                resources.getString(R.string.seat_times_price, count, price)
        }
    }

    private fun setDiscountPrice(ticket: Ticket?) {
        ticket?.let {
            if (ticket.promoCode.isNullOrEmpty()) {
                binding.discountPrice.isVisible = false
                return
            } else {
                val count = ticket.seats.size
                val price =
                    (ticket.price * count * (1 + ticket.rate * ticket.discountFactor)).roundToInt()
                        .toString()
                binding.discountPrice.isVisible = true
                binding.discountPrice.text =
                    resources.getString(R.string.price_sdg, price)
                binding.basePrice.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
            }
        }
    }

    private fun setBasePrice(ticket: Ticket?) {
        ticket?.let {
            val count = ticket.seats.size
            val price = ((1.0 + ticket.rate) * ticket.price * count).roundToInt().toString()
            binding.basePrice.text = resources.getString(R.string.price_sdg, price)
        }
    }

    private fun setStatus(state: TicketUiState) {
        binding.status.text = mapStatusLabel(state.ticket?.status!!)
        binding.status.setTextColor(mapStatusLabelColor(state.ticket.status))
    }

    private fun mapStatusLabel(status: TicketStatus): CharSequence {
        return when (status) {
            TicketStatus.PRE_BOOK -> getString(R.string.prebook)
            TicketStatus.PAYMENT_CONFIRMATION -> getString(R.string.payment_confirmation)
            TicketStatus.PAID -> getString(R.string.paid)
            TicketStatus.CANCELLED -> getString(R.string.cancelled)
            TicketStatus.DISPROVED -> getString(R.string.disproved)
        }
    }

    private fun mapStatusLabelColor(status: TicketStatus): Int {
        return when (status) {
            TicketStatus.PRE_BOOK -> requireContext().color(R.color.teal_700)
            TicketStatus.PAYMENT_CONFIRMATION -> requireContext().color(R.color.orange)
            TicketStatus.PAID -> requireContext().color(R.color.green)
            TicketStatus.CANCELLED ->
                requireContext().color(android.R.color.holo_red_dark)
            TicketStatus.DISPROVED ->
                requireContext().color(android.R.color.holo_red_dark)
        }
    }

    private fun setGenericButton(state: TicketUiState) {
        val status = state.ticket?.status!!
        if (!(status == TicketStatus.PRE_BOOK || status == TicketStatus.DISPROVED)) {
            binding.genericButton.hide()
            return
        }
        val label = mapButtonLabel(status)
        val onClick = mapButtonOnClick(status)

        binding.genericButton.show()
        binding.genericButton.text = label
        binding.genericButton.setOnClickListener(onClick)
    }

    private fun mapButtonLabel(status: TicketStatus): String? {
        return when (status) {
            TicketStatus.PRE_BOOK -> getString(R.string.pay_now)
            TicketStatus.DISPROVED -> getString(R.string.contact_us)
            else -> null
        }
    }

    private fun mapButtonOnClick(status: TicketStatus): View.OnClickListener? {
        return when (status) {
            TicketStatus.PRE_BOOK -> View.OnClickListener {
                findNavController().navigate(
                    TicketDetailFragmentDirections.actionTicketDetailToPaymentFragment(
                        args.ticketId
                    )
                )
            }
            TicketStatus.DISPROVED -> View.OnClickListener {
                // TODO: Enable contact via whatsapp
            }
            else -> null
        }
    }
}
