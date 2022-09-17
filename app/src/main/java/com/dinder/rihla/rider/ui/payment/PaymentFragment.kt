package com.dinder.rihla.rider.ui.payment

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.dinder.rihla.rider.data.model.PaymentInfo
import com.dinder.rihla.rider.data.model.Ticket
import com.dinder.rihla.rider.databinding.ConfirmPaymentBottomsheetDialogBinding
import com.dinder.rihla.rider.databinding.FragmentPaymentBinding
import com.dinder.rihla.rider.databinding.RedeemPromoCodeBottomsheetDialogBinding
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
class PaymentFragment : RihlaFragment() {
    private val viewModel: PaymentViewModel by viewModels()
    private val args: PaymentFragmentArgs by navArgs()
    private lateinit var binding: FragmentPaymentBinding

    @Inject
    lateinit var mixpanel: MixpanelAPI

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        val seatAdapter = StringItemsAdapter()
        binding.paymentSeatsRecyclerView.apply {
            adapter = seatAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.paidButton.hide()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getPaymentInfo(args.ticketId)
                viewModel.state.collect { state ->
                    state.messages.firstOrNull()?.let {
                        showSnackbar(it.content)
                        viewModel.userMessageShown(it.id)
                    }

                    if (state.paid) {
                        findNavController().navigateUp()
                        showPaymentSuccessfulDialog()
                    }

                    if (state.codeRedeemSuccessful) {
                        showSnackbar(getString(R.string.code_redeemed))
                        viewModel.retry(args.ticketId)
                    }

                    if (state.loading) {
                        binding.paidButton.hide()
                        binding.paymentLoading.isVisible = true
                        binding.paymentError.isVisible = false
                        return@collect
                    }

                    if (state.error) {
                        binding.paidButton.hide()
                        binding.paymentLoading.isVisible = false
                        binding.paymentError.isVisible = true
                        return@collect
                    }

                    if (!state.loading && !state.error) {
                        binding.paymentLoading.isVisible = false
                        binding.paymentError.isVisible = false
                        binding.paidButton.show()

                        binding.paymentInfo = state.paymentInfo
                        seatAdapter.submitList(state.paymentInfo?.ticket?.seats)

                        setSeatsTimesPrice(state.paymentInfo?.ticket)
                        setBasePrice(state.paymentInfo?.ticket)
                        setDiscountPrice(state.paymentInfo?.ticket)
                        setPaymentAccountInfo(state.paymentInfo)
                        setCopyButtons(state.paymentInfo)
                        setPaidButton(state.paymentInfo)
                        setPromoCode(state.paymentInfo)
                        setRetryButton()
                    }
                }
            }
        }
    }

    private fun setPromoCode(paymentInfo: PaymentInfo?) {
        if (!paymentInfo?.ticket?.promoCode.isNullOrEmpty()) {
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

    private fun setRetryButton() {
        binding.retryButton.setOnClickListener {
            viewModel.retry(args.ticketId)
        }
    }

    private fun setPaidButton(paymentInfo: PaymentInfo?) {
        paymentInfo?.let {
            binding.paidButton.setOnClickListener {
                showConfirmPaymentBottomSheetDialog(paymentInfo)
            }
        }
    }

    private fun showConfirmPaymentBottomSheetDialog(paymentInfo: PaymentInfo?) {
        paymentInfo?.let {
            val dialogBinding =
                ConfirmPaymentBottomsheetDialogBinding.inflate(layoutInflater, null, false)
            dialogBinding.paymentInfo = it

            val bottomSheetDialog = BottomSheetDialog(requireContext())
            bottomSheetDialog.setContentView(dialogBinding.root)
            bottomSheetDialog.show()

            dialogBinding.confirmPaymentButton.setOnClickListener {
                if (!NetworkUtils.isNetworkConnected(requireContext())) {
                    bottomSheetDialog.dismiss()
                    showSnackbar(resources.getString(R.string.no_network))
                    return@setOnClickListener
                }
                bottomSheetDialog.dismiss()
                viewModel.pay(paymentInfo.ticket)
            }
        }
    }

    private fun showPaymentSuccessfulDialog() {
        val dialog =
            AlertDialog.Builder(requireContext()).setView(R.layout.payment_successful_dialog)
        dialog.show()
    }

    private fun setCopyButtons(paymentInfo: PaymentInfo?) {
        val clipBoard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        binding.copyAccountNumber.setOnClickListener {
            val clipData = ClipData.newPlainText(
                resources.getString(R.string.account_number),
                paymentInfo?.accountNumber
            )
            clipBoard.setPrimaryClip(clipData)
            showSnackbar(resources.getString(R.string.copied))
        }

        binding.copyPhoneNumber.setOnClickListener {
            val clipData = ClipData.newPlainText(
                resources.getString(R.string.account_number),
                paymentInfo?.billingWhatsappNumber
            )
            clipBoard.setPrimaryClip(clipData)
            showSnackbar(resources.getString(R.string.copied))
        }
    }

    private fun setPaymentAccountInfo(paymentInfo: PaymentInfo?) {
        paymentInfo?.let {
            binding.accountNumber.text = it.accountNumber
            binding.accountName.text = it.accountName
            binding.billingWhatsappNumber.text = it.billingWhatsappNumber
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

    private fun setSeatsTimesPrice(ticket: Ticket?) {
        ticket?.let {
            val count = ticket.seats.size
            val price = ((1 + ticket.rate) * ticket.price).roundToInt().toString()
            binding.seatsTimePrice.text =
                resources.getString(R.string.seat_times_price, count, price)
        }
    }
}
