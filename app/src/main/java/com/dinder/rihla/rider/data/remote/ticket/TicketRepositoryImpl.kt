package com.dinder.rihla.rider.data.remote.ticket

import android.util.Log
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
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val tickets =
                            snapshot.documents.map { json -> Ticket.fromJson(json.data!!) }
                        trySend(Result.Success(tickets))
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
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.documents.isNotEmpty()) {
                    val ticket = Ticket.fromJson(snapshot.documents.first().data!!)
                    Log.i("observeTicket", "observeTicket: $ticket")
                    trySend(Result.Success(ticket))
                } else {
                    trySend(Result.Error(errorMessages.ticketNotFound))
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
                            Log.i("PAYMENT", "getPaymentInfo: ${result["data"]}")
                            val paymentInfo =
                                PaymentInfo.fromJson(result["data"] as Map<String, String>)
                            Log.i("PAYMENT", "getPaymentInfo: ${result["data"]}")
                            trySend(Result.Success(paymentInfo))
                        } else {
                            trySend(Result.Error(errorMessages.failedToReserveSeat))
                            Log.e("paymentInfo", "getPaymentInfo: FAILED")
                        }
                    }
                    .addOnFailureListener {
                        trySend(Result.Error(errorMessages.failedToReserveSeat))
                        Log.e("paymentInfo", "getPaymentInfo: FAILED", it)
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
                    Log.e("pay", "payment: SUCCESS")
                    trySend(Result.Success(isSuccessful))
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.failedToReserveSeat))
                    Log.e("pay", "getPaymentInfo: FAILED", it)
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
                        Log.e("redeemCode", "redeemCode: SUCCESS")
                        trySend(Result.Success(isSuccessful))
                    }
                    .addOnFailureListener {
                        trySend(Result.Error(errorMessages.failedToReserveSeat))
                        Log.e("redeemCode", "redeemCode: FAILED", it)
                    }
            }
            awaitClose()
        }
}
