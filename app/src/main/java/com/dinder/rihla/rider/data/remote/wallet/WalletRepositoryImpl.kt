package com.dinder.rihla.rider.data.remote.wallet

import android.util.Log
import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.utils.ErrorMessages
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
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
                        Log.i("WalletRepo", "getBalance: SUCCESS")
                        trySend(Result.Success(it.data!!["amount"].toString().toDouble()))
                    } else {
                        Log.e("WalletRepo", "getBalance: FAILED")
                        trySend(Result.Error("TODO: message"))
                    }
                }
                .addOnFailureListener {
                    Log.e("WalletRepo", "getBalance: FAILED", it)
                    trySend(Result.Error("TODO: message"))
                }
        }
        awaitClose()
    }
}
