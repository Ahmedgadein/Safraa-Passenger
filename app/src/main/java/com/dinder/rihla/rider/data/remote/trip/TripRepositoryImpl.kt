package com.dinder.rihla.rider.data.remote.trip

import android.util.Log
import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Fields
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Destination
import com.dinder.rihla.rider.data.model.Seat
import com.dinder.rihla.rider.data.model.Trip
import com.dinder.rihla.rider.utils.SeatUtils
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
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
            _ref.whereGreaterThanOrEqualTo(Fields.DATE, Date()).get()
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

                var query: Query = _ref.whereGreaterThanOrEqualTo(Fields.DATE, Date())
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

    override fun observeTrip(id: Long): Flow<Result<Trip>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.ID, id).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error("Trip observe Error"))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.documents.isNotEmpty()) {
                    val trip = Trip.fromJson(snapshot.documents.first().data!!)
                    trySend(Result.Success(trip))
                } else {
                    trySend(Result.Error("Trip not found"))
                }
            }
        }
        awaitClose()
    }

    override fun getTrip(id: Long): Flow<Result<Trip>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.ID, id).limit(1).get()
                .addOnSuccessListener {
                    val trip = Trip.fromJson(it.documents.first().data!!)
                    trySend(Result.Success(trip))
                }
                .addOnFailureListener {
                    trySend(Result.Error("Failed to load trip"))
                }
        }
        awaitClose()
    }

    override fun reserveSeats(tripId: Long, seats: List<Seat>): Flow<Result<Unit>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.ID, tripId).limit(1).get()
                .addOnSuccessListener {
                    _ref.document(it.documents[0].id).set(
                        mapOf(
                            Fields.SEATS to
                                SeatUtils.seatsModelToMap(seats)
                        ),
                        SetOptions.merge()
                    )
                        .addOnSuccessListener {
                            Log.i("UpdateSeatState", "updateSeatState status: Successful")
                            trySend(Result.Success(Unit))
                        }
                        .addOnFailureListener {
                            Log.i("UpdateSeatState", "updateSeatState status: Failure")
                            trySend(Result.Error("Failed to reserve seat"))
                        }
                }
                .addOnFailureListener {
                    trySend(Result.Error("Failed to find trip"))
                }
        }
        awaitClose()
    }
}
