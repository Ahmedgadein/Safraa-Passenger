package com.dinder.rihla.rider.data.remote.destination

import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Destination
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

@ExperimentalCoroutinesApi
class DestinationRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessages: ErrorMessages
) :
    DestinationRepository {
    private val _ref = Firebase.firestore.collection(Collections.DESTINATIONS)

    override fun getDestinations(): Flow<Result<List<Destination>>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.get()
                .addOnSuccessListener {
                    val destinations = it.documents.map { Destination.fromJson(it.data!!) }
                    trySend(Result.Success(destinations))
                    Timber.i("Got destinations: $destinations")
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.loadingDestinationsFailed))
                    Timber.e("FAILED to get destinations", it)
                }
        }
        awaitClose()
    }
}
