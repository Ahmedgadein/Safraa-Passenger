package com.dinder.rihla.rider.domain

import android.content.res.Resources
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.remote.version.AppVersionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateAppUseCase @Inject constructor(
    private val repository: AppVersionRepository,
    private val resources: Resources
) {
    suspend operator fun invoke(): Flow<Result<Boolean>> = flow {
        repository.get().collect { version ->
            when (version) {
                Result.Loading -> Unit
                is Result.Error -> emit(Result.Error(version.message))
                is Result.Success -> {
                    val currentVersion = resources.getString(R.string.app_version)
                    val shouldUpdateApp = version.value != currentVersion
                    emit(Result.Success(shouldUpdateApp))
                }
            }
        }
    }
}
