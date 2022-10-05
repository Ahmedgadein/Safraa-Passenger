package com.dinder.rihla.rider.domain

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Ticket
import com.dinder.rihla.rider.data.remote.ticket.TicketRepository
import com.dinder.rihla.rider.data.remote.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTicketsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val ticketRepository: TicketRepository
) {
    operator fun invoke(): Flow<Result<List<Ticket>>> = flow {
        userRepository.user.collect { user ->
            user?.let {
                ticketRepository.observeTickets(userId = user.id).collect { result ->
                    when (result) {
                        Result.Loading -> emit(Result.Loading)
                        is Result.Error -> emit(Result.Error(result.message))
                        is Result.Success -> emit(Result.Success(result.value))
                    }
                }
            }
        }
    }
}
