package com.dinder.rihla.rider.data.remote.user

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun get(id: String): Flow<Result<User>>
    suspend fun add(user: User)
    val user: Flow<User?>
}