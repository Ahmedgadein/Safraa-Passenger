package com.dinder.rihla.rider.data.remote.ticket

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.PaymentInfo
import com.dinder.rihla.rider.data.model.Ticket
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    suspend fun getTickets(userId: String): Flow<Result<List<Ticket>>>
    suspend fun getTicket(id: String): Flow<Result<Ticket>>
    suspend fun observeTicket(id: String): Flow<Result<Ticket>>
    suspend fun addTicket(ticket: Ticket): Flow<Result<Unit>>
    suspend fun getPaymentInfo(id: String): Flow<Result<PaymentInfo>>
    suspend fun pay(ticketId: String, tripId: String, seats: List<String>, amount: String):
        Flow<Result<Boolean>>

    suspend fun redeemCode(ticketId: String, code: String):
        Flow<Result<Boolean>>
}
