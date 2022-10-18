package com.dinder.rihla.rider.data.remote.auth

import android.content.SharedPreferences
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
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessages: ErrorMessages,
    private val preferences: SharedPreferences,
) : AuthRepository {
    private val _ref = Firebase.firestore.collection(Collections.USERS)

    override suspend fun isRegistered(phoneNumber: String): Flow<Result<Boolean>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.PHONE_NUMBER, phoneNumber).get()
                .addOnSuccessListener {
                    trySend(Result.Success(!it.isEmpty))
                    Timber.i("isUser Registered: ${!it.isEmpty}")
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.failedToResolveRegistration))
                    Timber.e("isRegistered ERROR: ", it)
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
                    val userMap = user.copy(token = token).toJson().toMutableMap()
                    userMap["invitedBy"] = preferences.getString("referrerId", null)
                    _ref.document(auth.currentUser?.uid!!).set(userMap)
                        .addOnSuccessListener {
                            Timber.i("User registration successful")
                            trySend(Result.Success(true))
                        }
                        .addOnFailureListener {
                            trySend(Result.Error(errorMessages.signupFailed))
                            Timber.e("User registration FAILED: ", it)
                        }
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.signupFailed))
                    Timber.e("User registration FAILED: ", it)
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
                    Timber.i("User Login Successful")
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.loginFailed))
                    Timber.e("User login FAILED: ", it)
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
