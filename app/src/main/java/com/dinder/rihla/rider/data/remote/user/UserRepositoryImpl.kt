package com.dinder.rihla.rider.data.remote.user

import com.dinder.rihla.rider.common.Collections
import com.dinder.rihla.rider.common.Fields
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.local.UserDao
import com.dinder.rihla.rider.data.model.User
import com.dinder.rihla.rider.utils.ErrorMessages
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
class UserRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val dao: UserDao,
    private val errorMessages: ErrorMessages
) :
    UserRepository {
    private val _ref = Firebase.firestore.collection(Collections.USERS)

    override suspend fun get(id: String): Flow<Result<User>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.ID, id).limit(1).get()
                .addOnSuccessListener {
                    trySend(Result.Success(User.fromJson(it.documents[0].data!!)))
                }
                .addOnSuccessListener {
                    trySend(Result.Error(errorMessages.couldntFindUser))
                }
        }
        awaitClose()
    }

    override suspend fun add(user: User) {
        withContext(ioDispatcher) {
            dao.insert(user)
        }
    }

    override val user: Flow<User?> = channelFlow {
        withContext(ioDispatcher) {
            val user = dao.getUser()
            send(user)
        }
    }
}
