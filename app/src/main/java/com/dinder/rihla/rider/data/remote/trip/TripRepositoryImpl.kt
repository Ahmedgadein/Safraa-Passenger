package com.dinder.rihla.rider.data.remote.trip

import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Fields
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Destination
import com.dinder.rihla.rider.data.model.Trip
import com.dinder.rihla.rider.utils.ErrorMessages
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@ExperimentalCoroutinesApi
class TripRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessages: ErrorMessages,
) :
    TripRepository {
    private val _ref = Firebase.firestore.collection(Collections.TRIPS)

    override fun getTrips(): Flow<Result<List<Trip>>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereGreaterThanOrEqualTo(Fields.DEPARTURE, Date()).get()
                .addOnSuccessListener {
                    val trips = it.documents.map { doc -> Trip.fromJson(doc.data!!) }
                    trySend(Result.Success(trips))
                    Timber.i("Got trips SUCCESS")
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.loadingTripsFailed))
                    Timber.e("FAILED to get trips", it)
                }
        }
        awaitClose()
    }

    override fun queryTrips(from: Destination?, to: Destination?): Flow<Result<List<Trip>>> =
        callbackFlow {
            withContext(ioDispatcher) {
                trySend(Result.Loading)

                var query: Query = _ref.whereGreaterThanOrEqualTo(Fields.DEPARTURE, Date())
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
                        Timber.i("Query trips SUCCESS")
                    }
                    .addOnFailureListener {
                        trySend(Result.Error(errorMessages.loadingTripsFailed))
                        Timber.e("FAILED to query trips: ", it)
                    }
            }
            awaitClose()
        }

    override fun observeTrip(id: String): Flow<Result<Trip>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.ID, id).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(errorMessages.tripNotFound))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.documents.isNotEmpty()) {
                    val trip = Trip.fromJson(snapshot.documents.first().data!!)
                    trySend(Result.Success(trip))
                    Timber.i("Observing trip: $trip")
                } else {
                    trySend(Result.Error(errorMessages.tripNotFound))
                    Timber.e("Observing empty trip")
                }
            }
        }
        awaitClose()
    }

    override fun getTrip(id: String): Flow<Result<Trip>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.ID, id).limit(1).get()
                .addOnSuccessListener {
                    val trip = Trip.fromJson(it.documents.first().data!!)
                    trySend(Result.Success(trip))
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.failedToLoadTrip))
                }
        }
        awaitClose()
    }

    override fun reserveSeats(
        tripId: String,
        name: String,
        seats: List<String>,
    ): Flow<Result<String>> =
        callbackFlow {
            withContext(ioDispatcher) {
                trySend(Result.Loading)
                // Create the arguments to the callable function.
                val data = mapOf(
                    "tripId" to tripId,
                    "seats" to seats,
                    "name" to name
                )

                FirebaseFunctions.getInstance()
                    .getHttpsCallable("trips-bookSeats")
                    .call(data)
                    .addOnSuccessListener {
                        val result: Map<String, Any> = it.data as Map<String, Any>
                        val isSuccessful = result["success"] as Boolean
                        if (isSuccessful) {
                            val ticketId = (result["data"] as Map<String, String>)["ticketId"]!!
                            trySend(Result.Success(ticketId))
                            Timber.i("Reserved seats SUCCESS")
                        } else {
                            trySend(Result.Error(errorMessages.failedToReserveSeat))
                            Timber.e("FAILED to reserve seats")
                        }
                    }
                    .addOnFailureListener {
                        trySend(Result.Error(errorMessages.failedToReserveSeat))
                        Timber.e("FAILED to reserve seats: ", it)
                    }
            }
            awaitClose()
        }
}
