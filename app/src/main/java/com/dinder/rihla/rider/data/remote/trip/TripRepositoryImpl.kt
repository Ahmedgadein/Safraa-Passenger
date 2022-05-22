package com.dinder.rihla.rider.data.remote.trip

import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Fields
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Destination
import com.dinder.rihla.rider.data.model.Trip
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@ExperimentalCoroutinesApi
class TripRepositoryImpl @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
    TripRepository {
    private val _ref = Firebase.firestore.collection(Collections.TRIPS)

    override fun getTrips(): Flow<Result<List<Trip>>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereLessThanOrEqualTo(Fields.DATE, Date()).get()
                .addOnSuccessListener {
                    val trips = it.documents.map { doc -> Trip.fromJson(doc.data!!) }
                    trySend(Result.Success(trips))
                }
                .addOnFailureListener {
                    trySend(Result.Error("Failed to load trips"))
                }
        }
        awaitClose()
    }

    override fun queryTrips(from: Destination?, to: Destination?): Flow<Result<List<Trip>>> =
        callbackFlow {
            withContext(ioDispatcher) {
                trySend(Result.Loading)

                var query: Query = _ref.whereLessThanOrEqualTo(Fields.DATE, Date())
                from?.let {
                    query = query.whereEqualTo(Fields.FROM, from.toJson())
                }
                to?.let {
                    query = query.whereEqualTo(Fields.TO, to.toJson())
                }

                query.get()
                    .addOnSuccessListener {
                        val trips = it.documents.map { json -> Trip.fromJson(json.data!!) }
                        trySend(Result.Success(trips))
                    }
                    .addOnFailureListener {
                        trySend(Result.Error("Failed to query trips"))
                    }
            }
            awaitClose()
        }
}
