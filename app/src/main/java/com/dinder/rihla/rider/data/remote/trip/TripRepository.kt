package com.dinder.rihla.rider.data.remote.trip

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Destination
import com.dinder.rihla.rider.data.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getTrips(): Flow<Result<List<Trip>>>
    fun queryTrips(from: Destination?, to: Destination?): Flow<Result<List<Trip>>>
}
