package com.dinder.rihla.rider.domain

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Trip
import com.dinder.rihla.rider.data.remote.rates.RateRepository
import com.dinder.rihla.rider.data.remote.trip.TripRepository
import com.dinder.rihla.rider.utils.ErrorMessages
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAgentTripsUseCase @Inject constructor(
    private val tripRepository: TripRepository,
    private val rateRepository: RateRepository,
    private val errorMessages: ErrorMessages,
) {
    operator fun invoke(): Flow<Result<List<Trip>>> = flow {
        rateRepository.getRates().collect { rateResult ->
            when (rateResult) {
                Result.Loading -> emit(Result.Loading)
                is Result.Error -> emit(Result.Error(errorMessages.loadingTripsFailed))
                is Result.Success -> {
                    tripRepository.getTrips().collect { tripsResult ->
                        when (tripsResult) {
                            Result.Loading -> Unit
                            is Result.Error -> emit(Result.Error(errorMessages.loadingTripsFailed))
                            is Result.Success -> {
                                val trips =
                                    tripsResult.value.map { it.copy(agentRate = rateResult.value.agent) }
                                        .sortedByDescending { it.price }
                                emit(Result.Success(trips))
                            }
                        }
                    }
                }
            }
        }
    }
}
