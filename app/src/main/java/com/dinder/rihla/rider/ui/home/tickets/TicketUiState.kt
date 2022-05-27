package com.dinder.rihla.rider.ui.home.tickets

import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.data.model.Ticket

data class TicketUiState(
    val loading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val tickets: List<Ticket> = emptyList()
)
