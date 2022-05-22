package com.dinder.rihla.rider.ui.home.trips

import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.data.model.Destination
import com.dinder.rihla.rider.data.model.Trip

data class TripsUiState(
    val loading: Boolean = false,
    val trips: List<Trip> = emptyList(),
    val fromDestination: Destination? = null,
    val toDestination: Destination? = null,
    val destinations: List<Destination> = emptyList(),
    val messages: List<Message> = emptyList()
)
