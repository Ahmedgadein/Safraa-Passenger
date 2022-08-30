package com.dinder.rihla.rider.data.remote.auth

import android.util.Log
import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Fields
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.User
import com.dinder.rihla.rider.utils.ErrorMessages
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessages: ErrorMessages,
) : AuthRepository {
    private val _ref = Firebase.firestore.collection(Collections.USERS)

    override suspend fun isRegistered(phoneNumber: String): Flow<Result<Boolean>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.PHONE_NUMBER, phoneNumber).get()
                .addOnSuccessListener {
                    trySend(Result.Success(!it.isEmpty))
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.failedToResolveRegistration))
                }
        }
        awaitClose()
    }

    override suspend fun isLoggedIn(): Flow<Result<Boolean>> = flow {
        emit(Result.Success(auth.currentUser != null))
    }

    override suspend fun register(user: User): Flow<Result<Boolean>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    _ref.add(user.copy(token = token).toJson())
                        .addOnSuccessListener {
                            trySend(Result.Success(true))
                        }
                        .addOnFailureListener {
                            trySend(Result.Error(errorMessages.signupFailed))
                        }
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.signupFailed))
                }
        }
        awaitClose()
    }

    override suspend fun login(credential: AuthCredential): Flow<Result<Boolean>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    trySend(Result.Success(true))
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.loginFailed))
                }
        }
        awaitClose()
    }

    override suspend fun updateToken(id: String, token: String): Boolean {
        try {
            val user = _ref.whereEqualTo(Fields.ID, id).get().await()
            if (user.isEmpty) {
                return false
            }
            user.documents.first().reference.set(
                mapOf(
                    "token" to token
                ),
                SetOptions.merge()
            ).await()

            return true
        } catch (e: Exception) {
            Log.e("Token", e.toString())
            return false
        }
    }
}
