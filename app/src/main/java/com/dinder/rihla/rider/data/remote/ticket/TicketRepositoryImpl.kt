package com.dinder.rihla.rider.data.remote.ticket

import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Fields
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.PaymentInfo
import com.dinder.rihla.rider.data.model.Ticket
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
import javax.inject.Inject

@ExperimentalCoroutinesApi
class TicketRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessages: ErrorMessages,
) :
    TicketRepository {
    private val _ref = Firebase.firestore.collection(Collections.TICKETS)

    override suspend fun observeTickets(userId: String): Flow<Result<List<Ticket>>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.PASSENGER_ID, userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        trySend(Result.Error(errorMessages.loadingTicketsFailed))
                        Timber.e("Observing ticketS error: ", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val tickets =
                            snapshot.documents.map { json -> Ticket.fromJson(json.data!!) }
                        trySend(Result.Success(tickets))
                        Timber.i("Observing ticketS: ", tickets)
                    }
                }
        }
        awaitClose()
    }

    override suspend fun observeTicket(id: String): Flow<Result<Ticket>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.ID, id).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(errorMessages.ticketNotFound))
                    Timber.e("Observing ticket error: ", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.documents.isNotEmpty()) {
                    val ticket = Ticket.fromJson(snapshot.documents.first().data!!)
                    trySend(Result.Success(ticket))
                    Timber.i("Observing ticket: ", ticket)
                } else {
                    trySend(Result.Error(errorMessages.ticketNotFound))
                    Timber.e("Observe empty ticket Error: ", snapshot)
                }
            }
        }
        awaitClose()
    }

    override suspend fun getPaymentInfo(id: String): Flow<Result<PaymentInfo>> =
        callbackFlow {
            withContext(ioDispatcher) {
                trySend(Result.Loading)
                // Create the arguments to the callable function.
                val data = mapOf(
                    "id" to id,
                )

                FirebaseFunctions.getInstance()
                    .getHttpsCallable("payment-getTicketPaymentInfo")
                    .call(data)
                    .addOnSuccessListener {
                        val result: Map<String, Any> = it.data as Map<String, Any>
                        val isSuccessful = result["success"] as Boolean
                        if (isSuccessful) {
                            val paymentInfo =
                                PaymentInfo.fromJson(result["data"] as Map<String, String>)
                            trySend(Result.Success(paymentInfo))
                            Timber.i("Got payment data: ", result)
                        } else {
                            trySend(Result.Error(errorMessages.failedToReserveSeat))
                            Timber.e("Failed to get payment data: ", result)
                        }
                    }
                    .addOnFailureListener {
                        trySend(Result.Error(errorMessages.failedToReserveSeat))
                        Timber.e("FAILED to get payment data: ", it)
                    }
            }
            awaitClose()
        }

    override suspend fun pay(
        ticketId: String,
        tripId: String,
        seats: List<String>,
        amount: String,
    ): Flow<Result<Boolean>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            // Create the arguments to the callable function.
            val data = mapOf(
                "ticketId" to ticketId,
                "tripId" to tripId,
                "seats" to seats,
                "amount" to amount
            )

            FirebaseFunctions.getInstance()
                .getHttpsCallable("payment-pay")
                .call(data)
                .addOnSuccessListener {
                    val result: Map<String, Any> = it.data as Map<String, Any>
                    val isSuccessful = result["success"] as Boolean
                    trySend(Result.Success(isSuccessful))
                    Timber.i("Payment SUCCESSFUL")
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.failedToReserveSeat))
                    Timber.e("Payment FAILURE: ", it)
                }
        }
        awaitClose()
    }

    override suspend fun redeemCode(ticketId: String, code: String): Flow<Result<Boolean>> =
        callbackFlow {
            withContext(ioDispatcher) {
                trySend(Result.Loading)
                // Create the arguments to the callable function.
                val data = mapOf(
                    "ticketId" to ticketId,
                    "code" to code,
                )

                FirebaseFunctions.getInstance()
                    .getHttpsCallable("payment-redeemCode")
                    .call(data)
                    .addOnSuccessListener {
                        val result: Map<String, Any> = it.data as Map<String, Any>
                        val isSuccessful = result["success"] as Boolean
                        trySend(Result.Success(isSuccessful))
                        Timber.i("Redeem code: $code SUCCESSFUL")
                    }
                    .addOnFailureListener {
                        trySend(Result.Error(errorMessages.failedToReserveSeat))
                        Timber.e("Redeem code: $code FAILED")
                    }
            }
            awaitClose()
        }
}
