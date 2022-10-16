package com.dinder.rihla.rider.data.remote.rates

import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Rates
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
class RateRepositoryImpl @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
    RateRepository {

    private val _ref = Firebase.firestore.collection(Collections.CONSTANTS)

    override fun getRates(): Flow<Result<Rates>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.document(Collections.RATES).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val rates = Rates.fromJson(it.data!!)
                        trySend(Result.Success(rates))
                        Timber.d("getRates: SUCCESS: $rates")
                    } else {
                        trySend(Result.Error("Failed to load rate"))
                        Timber.e("Couldn't find rates: ", it)
                    }
                }
                .addOnFailureListener {
                    trySend(Result.Error("Failed to load rate"))
                    Timber.e("FAILED to get rates: ", it)
                }
        }
        awaitClose()
    }

    override fun observeRates(): Flow<Result<Rates>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref
                .document(Collections.RATES)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // TODO: Show meaningful error message
                        trySend(Result.Error("error"))
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val rates = Rates.fromJson(snapshot.data!!)
                        trySend(Result.Success(rates))
                        Timber.i("Observing rates: $rates")
                    } else {
                        // TODO: Show meaningful error message
                        trySend(Result.Error("error"))
                        Timber.e("Observing empty rates")
                    }
                }
        }
        awaitClose()
    }
}
