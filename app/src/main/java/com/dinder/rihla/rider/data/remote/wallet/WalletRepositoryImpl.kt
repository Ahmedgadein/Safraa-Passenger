package com.dinder.rihla.rider.data.remote.wallet

import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Transaction
import com.dinder.rihla.rider.utils.ErrorMessages
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class WalletRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessages: ErrorMessages,
) : WalletRepository {
    private val _ref = Firebase.firestore.collection(Collections.WALLET)

    override fun getBalance(userId: String): Flow<Result<Double>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.document(userId).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val amount = it.data!!["amount"].toString().toDouble()
                        trySend(Result.Success(amount))
                        Timber.i("getBalance Successful, balance: $amount")
                    } else {
                        trySend(Result.Error("TODO: message"))
                        Timber.e("FAILED to get balance")
                    }
                }
                .addOnFailureListener {
                    trySend(Result.Error("TODO: message"))
                    Timber.e("FAILED to get balance: ", it)
                }
        }
        awaitClose()
    }

    override fun getTransactions(userId: String): Flow<Result<List<Transaction>>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.document(userId)
                .collection(Collections.TRANSACTIONS)
                .get()
                .addOnSuccessListener {
                    val transactions = it.documents.map { Transaction.fromJson(it.data!!) }
                    trySend(Result.Success(transactions))
                    Timber.i("Got transactions successfully: $transactions")
                }
                .addOnFailureListener {
                    trySend(Result.Error("//TODO: Message"))
                    Timber.e("FAILED to get transactions: ", it)
                }
        }
        awaitClose()
    }
}
