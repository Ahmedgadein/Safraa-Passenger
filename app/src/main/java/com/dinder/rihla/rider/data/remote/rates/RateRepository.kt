package com.dinder.rihla.rider.data.remote.rates

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Rates
import kotlinx.coroutines.flow.Flow

interface RateRepository {
    fun getRates(): Flow<Result<Rates>>
}
