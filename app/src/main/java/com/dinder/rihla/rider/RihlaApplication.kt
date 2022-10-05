package com.dinder.rihla.rider

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.dinder.rihla.rider.services.RefreshTokenWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class RihlaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        updateToken()
        Timber.plant(Timber.DebugTree())
    }

    private fun updateToken() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicRefreshTokenWork = PeriodicWorkRequest.Builder(
            RefreshTokenWorker::class.java,
            14,
            TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        val uniqueWorkName = "refresh-token-work"

        WorkManager
            .getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRefreshTokenWork
            )
    }
}
