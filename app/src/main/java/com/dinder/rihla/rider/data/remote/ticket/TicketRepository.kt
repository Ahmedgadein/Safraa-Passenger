package com.dinder.rihla.rider.data.remote.ticket

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Ticket
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    suspend fun getTickets(userId: String): Flow<Result<List<Ticket>>>
    suspend fun getTicket(id: String): Flow<Result<Ticket>>
    suspend fun addTicket(ticket: Ticket): Flow<Result<Unit>>
}
