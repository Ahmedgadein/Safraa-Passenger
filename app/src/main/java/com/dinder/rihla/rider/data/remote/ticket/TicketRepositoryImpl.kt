package com.dinder.rihla.rider.data.remote.ticket

import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Fields
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Ticket
import com.dinder.rihla.rider.utils.ErrorMessages
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
class TicketRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessages: ErrorMessages
) :
    TicketRepository {
    private val _ref = Firebase.firestore.collection(Collections.TICKETS)

    override suspend fun getTickets(userId: String): Flow<Result<List<Ticket>>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.PASSENGER_ID, userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    val tickets = it.documents.map { json -> Ticket.fromJson(json.data!!) }
                    trySend(Result.Success(tickets))
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.loadingTicketsFailed))
                }
        }
        awaitClose()
    }

    override suspend fun getTicket(id: String): Flow<Result<Ticket>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.document(id).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val ticket = Ticket.fromJson(it.data!!)
                        trySend(Result.Success(ticket))
                    } else {
                        trySend(Result.Error(errorMessages.ticketNotFound))
                    }
                }
        }
        awaitClose()
    }

    override suspend fun addTicket(ticket: Ticket): Flow<Result<Unit>> = callbackFlow {
        withContext(ioDispatcher) {
            _ref.document().get()
                .addOnSuccessListener {
                    val id = it.id
                    it.reference.set(ticket.copy(id = id, createdAt = Date()).toJson())
                        .addOnSuccessListener {
                            trySend(Result.Success(Unit))
                        }
                        .addOnFailureListener {
                            trySend(Result.Error(errorMessages.failedToSaveTicket))
                        }
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.failedToSaveTicket))
                }
        }
        awaitClose()
    }
}
