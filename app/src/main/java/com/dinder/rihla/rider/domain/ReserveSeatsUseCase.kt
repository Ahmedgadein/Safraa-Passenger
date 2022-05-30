package com.dinder.rihla.rider.domain

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Seat
import com.dinder.rihla.rider.data.model.Ticket
import com.dinder.rihla.rider.data.remote.ticket.TicketRepository
import com.dinder.rihla.rider.data.remote.trip.TripRepository
import com.dinder.rihla.rider.data.remote.user.UserRepository
import com.dinder.rihla.rider.utils.SeatState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReserveSeatsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val tripRepository: TripRepository,
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(tripId: Long, seats: List<Seat>): Flow<Result<Unit>> = flow {
        userRepository.user.collect { user ->
            user?.let {
                val name = user.name
                val namedSeats = seats.map { it.copy(passenger = name, status = SeatState.BOOKED) }

                tripRepository.reserveSeats(tripId, namedSeats).collect { result ->
                    when (result) {
                        Result.Loading -> emit(Result.Loading)
                        is Result.Error -> emit(Result.Error(result.message))
                        is Result.Success -> tripRepository.getTrip(tripId).collect { tripResult ->
                            when (tripResult) {
                                Result.Loading -> Unit
                                is Result.Error -> emit(Result.Error(tripResult.message))
                                is Result.Success -> {
                                    val trip = tripResult.value
                                    val seats = seats.map { it.number.toString() }
                                    val ticket =
                                        Ticket(
                                            passengerId = user.id,
                                            passengerName = user.name,
                                            seats = seats,
                                            trip = trip
                                        )
                                    ticketRepository.addTicket(ticket).collect { ticketResult ->
                                        when (ticketResult) {
                                            Result.Loading -> Unit
                                            is Result.Error -> emit(
                                                Result.Error(ticketResult.message)
                                            )
                                            is Result.Success -> emit(Result.Success(Unit))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
