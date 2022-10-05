package com.dinder.rihla.rider.services

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dinder.rihla.rider.data.remote.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class RefreshTokenWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: AuthRepository,
    private val auth: FirebaseAuth
) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            val id = auth.uid.toString()

            return@withContext try {
                val token = FirebaseMessaging.getInstance().token.await()
                val tokenUpdated = repository.updateToken(id, token)
                Timber.i("Updating token: $token")
                if (!tokenUpdated) {
                    Timber.e("Updating token failed, token: $token")
                    Result.Retry()
                } else {
                    Timber.i("Updating token successful, token: $token")
                    Result.Success()
                }
            } catch (e: Exception) {
                Timber.e("Updating token failed: ", e)
                Result.Retry()
            }
        }
}
