package com.dinder.rihla.rider.ui.agent.trips

import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.data.model.Trip
import com.dinder.rihla.rider.data.model.User

data class AgentTripsUiState(
    val loading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val trips: List<Trip> = emptyList(),
    val user: User? = null
)
