package com.dinder.rihla.rider.data.remote.destination

import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Destination
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DestinationRepositoryImpl @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
    DestinationRepository {
    private val _ref = Firebase.firestore.collection(Collections.DESTINATIONS)

    override fun getDestinations(): Flow<Result<List<Destination>>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.get()
                .addOnSuccessListener {
                    val destinations = it.documents.map { Destination.fromJson(it.data!!) }
                    trySend(Result.Success(destinations))
                }
                .addOnFailureListener {
                    trySend(Result.Error("Failed to load destinations"))
                }
        }
        awaitClose()
    }
}
