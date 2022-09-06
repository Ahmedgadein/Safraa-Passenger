package com.dinder.rihla.rider.data.remote.wallet

import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Transaction
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getBalance(userId: String): Flow<Result<Double>>
    fun getTransactions(userId: String): Flow<Result<List<Transaction>>>
}
