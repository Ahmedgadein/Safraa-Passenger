package com.dinder.rihla.rider.data.remote.destination

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Destination
import kotlinx.coroutines.flow.Flow

interface DestinationRepository {
    fun getDestinations(): Flow<Result<List<Destination>>>
}
