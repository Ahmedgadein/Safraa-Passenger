package com.dinder.rihla.rider.data.remote.auth

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.User
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun isRegistered(phoneNumber: String): Flow<Result<Boolean>>
    suspend fun isLoggedIn(): Flow<Result<Boolean>>
    suspend fun register(user: User): Flow<Result<Boolean>>
    suspend fun login(credential: AuthCredential): Flow<Result<Boolean>>
}
