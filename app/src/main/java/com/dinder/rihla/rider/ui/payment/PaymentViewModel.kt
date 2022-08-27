package com.dinder.rihla.rider.ui.payment

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Ticket
import com.dinder.rihla.rider.data.remote.ticket.TicketRepository
import com.dinder.rihla.rider.utils.PriceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: TicketRepository,
    private val resources: Resources,
) : ViewModel() {
    private val _state = MutableStateFlow(PaymentUiState())
    val state = _state.asStateFlow()

    fun getPaymentInfo(ticketId: String) {
        viewModelScope.launch {
            repository.getPaymentInfo(ticketId).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> _state.update { it.copy(loading = false, error = true) }
                    is Result.Success -> _state.update {
                        it.copy(
                            loading = false,
                            paymentInfo = result.value
                        )
                    }
                }
            }
        }
    }

    fun retry(ticketId: String) {
        _state.update { it.copy(error = false, loading = false, codeRedeemSuccessful = false) }
        getPaymentInfo(ticketId)
    }

    fun pay(ticket: Ticket) {
        viewModelScope.launch {
            val amount = PriceUtils.getPrice(ticket)
            repository.pay(ticket.id, ticket.tripId, ticket.seats, amount).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(resources.getString(R.string.payment_failed))
                    is Result.Success -> _state.update { it.copy(loading = false, paid = true) }
                }
            }
        }
    }

    fun redeemCode(ticketId: String, code: String) {
        viewModelScope.launch {
            repository.redeemCode(ticketId, code).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true, error = false) }
                    is Result.Error -> showUserMessage(resources.getString(R.string.failed_to_redeem_code))
                    is Result.Success -> {
                        if (result.value) {
                            _state.update {
                                it.copy(
                                    loading = false,
                                    codeRedeemSuccessful = true
                                )
                            }
                        } else {
                            _state.update { it.copy(loading = false) }
                            showUserMessage(resources.getString(R.string.redeem_code_incorrect))
                        }
                    }
                }
            }
        }
    }

    private fun showUserMessage(content: String) {
        _state.update { state ->
            val messages = state.messages + Message(
                id = UUID.randomUUID().mostSignificantBits,
                content = content
            )
            state.copy(messages = messages, loading = false)
        }
    }

    fun userMessageShown(messageId: Long) {
        _state.update { state ->
            val messages = state.messages.filterNot { it.id == messageId }
            state.copy(messages = messages)
        }
    }
}
