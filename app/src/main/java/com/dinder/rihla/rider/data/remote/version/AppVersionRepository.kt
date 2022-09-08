package com.dinder.rihla.rider.data.remote.version

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.UpdateApp
import kotlinx.coroutines.flow.Flow

interface AppVersionRepository {
    fun get(): Flow<Result<UpdateApp>>
}
