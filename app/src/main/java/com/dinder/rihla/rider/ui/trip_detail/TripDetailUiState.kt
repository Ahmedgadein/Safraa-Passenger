package com.dinder.rihla.rider.ui.trip_detail

import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.data.model.Trip

data class TripDetailUiState(
    val loading: Boolean = false,
    val isReserved: Boolean = false,
    val ticketID: String = "",
    val trip: Trip? = null,
    val messages: List<Message> = emptyList()
)
