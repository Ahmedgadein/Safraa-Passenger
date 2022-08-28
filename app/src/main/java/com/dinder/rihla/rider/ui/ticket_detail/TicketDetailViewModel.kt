package com.dinder.rihla.rider.ui.ticket_detail

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.remote.ticket.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TicketDetailViewModel @Inject constructor(
    private val ticketRepository: TicketRepository,
    private val resources: Resources,
) :
    ViewModel() {
    private val _state = MutableStateFlow(TicketUiState())
    val state = _state.asStateFlow()

    fun observeTicket(id: String) {
        viewModelScope.launch {
            ticketRepository.observeTicket(id).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> _state.update {
                        it.copy(
                            loading = false,
                            ticket = result.value
                        )
                    }
                }
            }
        }
    }

    fun redeemCode(ticketId: String, code: String) {
        viewModelScope.launch {
            ticketRepository.redeemCode(ticketId, code).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(resources.getString(R.string.failed_to_redeem_code))
                    is Result.Success -> {
                        if (result.value) {
                            _state.update {
                                it.copy(
                                    loading = false,
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
