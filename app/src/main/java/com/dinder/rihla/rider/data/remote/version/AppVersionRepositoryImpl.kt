package com.dinder.rihla.rider.data.remote.version

import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.UpdateApp
import com.dinder.rihla.rider.utils.ErrorMessages
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class AppVersionRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessages: ErrorMessages
) : AppVersionRepository {

    private val _ref = Firebase.firestore.collection(Collections.CONSTANTS)

    override fun get(): Flow<Result<UpdateApp>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.document(Collections.APP_VERSION).get()
                .addOnSuccessListener {
                    trySend(Result.Success(UpdateApp.fromJson(it.data!!)))
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.failedToResolveAppVersion))
                }
        }
        awaitClose()
    }
}
