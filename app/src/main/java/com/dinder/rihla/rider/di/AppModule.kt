package com.dinder.rihla.rider.di

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.preference.PreferenceManager
import com.dinder.rihla.rider.data.local.UserDao
import com.dinder.rihla.rider.data.local.db.RihlaDatabase
import com.dinder.rihla.rider.data.remote.auth.AuthRepository
import com.dinder.rihla.rider.data.remote.auth.FirebaseAuthRepository
import com.dinder.rihla.rider.data.remote.destination.DestinationRepository
import com.dinder.rihla.rider.data.remote.destination.DestinationRepositoryImpl
import com.dinder.rihla.rider.data.remote.rates.RateRepository
import com.dinder.rihla.rider.data.remote.rates.RateRepositoryImpl
import com.dinder.rihla.rider.data.remote.ticket.TicketRepository
import com.dinder.rihla.rider.data.remote.ticket.TicketRepositoryImpl
import com.dinder.rihla.rider.data.remote.trip.TripRepository
import com.dinder.rihla.rider.data.remote.trip.TripRepositoryImpl
import com.dinder.rihla.rider.data.remote.user.UserRepository
import com.dinder.rihla.rider.data.remote.user.UserRepositoryImpl
import com.dinder.rihla.rider.data.remote.version.AppVersionRepository
import com.dinder.rihla.rider.data.remote.version.AppVersionRepositoryImpl
import com.dinder.rihla.rider.data.remote.wallet.WalletRepository
import com.dinder.rihla.rider.data.remote.wallet.WalletRepositoryImpl
import com.dinder.rihla.rider.utils.ErrorMessages
import com.google.firebase.auth.FirebaseAuth
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@ExperimentalCoroutinesApi
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources = context.resources

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): RihlaDatabase =
        RihlaDatabase.getInstance(context)

    @Provides
    fun provideUserDao(database: RihlaDatabase): UserDao = database.userDao()

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
        dispatcher: CoroutineDispatcher,
        errorMessages: ErrorMessages,
        preferences: SharedPreferences
    ):
        AuthRepository =
            FirebaseAuthRepository(auth, dispatcher, errorMessages, preferences)

    @Provides
    fun provideUserRepository(
        dispatcher: CoroutineDispatcher,
        dao: UserDao,
        errorMessages: ErrorMessages,
    ): UserRepository =
        UserRepositoryImpl(dispatcher, dao, errorMessages)

    @Provides
    fun provideTripRepository(
        dispatcher: CoroutineDispatcher,
        errorMessages: ErrorMessages,
    ): TripRepository =
        TripRepositoryImpl(dispatcher, errorMessages)

    @Provides
    fun provideDestinationRepository(
        dispatcher: CoroutineDispatcher,
        errorMessages: ErrorMessages,
    ): DestinationRepository =
        DestinationRepositoryImpl(dispatcher, errorMessages)

    @Provides
    fun provideTicketRepository(
        dispatcher: CoroutineDispatcher,
        errorMessages: ErrorMessages,
    ): TicketRepository =
        TicketRepositoryImpl(dispatcher, errorMessages)

    @Provides
    fun provideAppVersionRepository(
        dispatcher: CoroutineDispatcher,
        errorMessages: ErrorMessages,
    ): AppVersionRepository =
        AppVersionRepositoryImpl(dispatcher, errorMessages)

    @Provides
    fun provideMixpanel(@ApplicationContext context: Context): MixpanelAPI =
        MixpanelAPI.getInstance(context, "244608d9170c936a37d24ef9a7b8eccf")

    @Provides
    fun provideRatesRepository(
        dispatcher: CoroutineDispatcher,
    ): RateRepository =
        RateRepositoryImpl(dispatcher)

    @Provides
    fun provideWalletRepository(
        dispatcher: CoroutineDispatcher,
        errorMessages: ErrorMessages,
    ): WalletRepository =
        WalletRepositoryImpl(dispatcher, errorMessages)
}
