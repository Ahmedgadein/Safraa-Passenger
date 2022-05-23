package com.dinder.rihla.rider.domain

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Seat
import com.dinder.rihla.rider.data.remote.trip.TripRepository
import com.dinder.rihla.rider.data.remote.user.UserRepository
import com.dinder.rihla.rider.utils.SeatState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReserveSeatsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val tripRepository: TripRepository
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
                        is Result.Success -> emit(Result.Success(Unit))
                    }
                }
            }
        }
    }
}
