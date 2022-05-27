package com.dinder.rihla.rider.data.remote.ticket

import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Ticket
import com.google.firebase.firestore.SetOptions
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
class TicketRepositoryImpl @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
    TicketRepository {
    private val _ref = Firebase.firestore.collection(Collections.TICKETS)

    override suspend fun getTickets(userId: String): Flow<Result<List<Ticket>>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.document(userId).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        var ticketsList = it.data!![Collections.TICKETS]
                        if (ticketsList == null) {
                            trySend(Result.Success(emptyList()))
                        } else {
                            ticketsList = ticketsList as List<Map<String, Any>>
                            val tickets = ticketsList.map { json -> Ticket.fromJson(json) }
                            trySend(Result.Success(tickets))
                        }
                    } else {
                        trySend(Result.Success(emptyList()))
                    }
                }
                .addOnFailureListener {
                    trySend(Result.Error("Failed to load tickets"))
                }
        }
        awaitClose()
    }

    override suspend fun addTicket(ticket: Ticket): Flow<Result<Unit>> = callbackFlow {
        withContext(ioDispatcher) {
            _ref.document(ticket.passengerId).set(
                mapOf(
                    Collections.TICKETS to listOf(
                        ticket.toJson()
                    )
                ),
                SetOptions.merge()
            )
                .addOnSuccessListener {
                    trySend(Result.Success(Unit))
                }
                .addOnFailureListener {
                    trySend(Result.Error("Failed to save ticket"))
                }
        }
        awaitClose()
    }
}
