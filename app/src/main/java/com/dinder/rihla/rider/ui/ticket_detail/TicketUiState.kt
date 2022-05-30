package com.dinder.rihla.rider.ui.ticket_detail

import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.data.model.Ticket

data class TicketUiState(
    val loading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val ticket: Ticket? = null
)
