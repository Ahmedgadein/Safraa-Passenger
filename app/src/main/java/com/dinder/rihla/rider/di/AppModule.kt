package com.dinder.rihla.rider.di

import android.content.Context
import com.dinder.rihla.rider.data.local.UserDao
import com.dinder.rihla.rider.data.local.db.RihlaDatabase
import com.dinder.rihla.rider.data.remote.auth.AuthRepository
import com.dinder.rihla.rider.data.remote.auth.FirebaseAuthRepository
import com.dinder.rihla.rider.data.remote.user.UserRepository
import com.dinder.rihla.rider.data.remote.user.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
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
    fun provideDatabase(@ApplicationContext context: Context): RihlaDatabase =
        RihlaDatabase.getInstance(context)

    @Provides
    fun provideUserDao(database: RihlaDatabase): UserDao = database.userDao()

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(auth: FirebaseAuth, dispatcher: CoroutineDispatcher):
        AuthRepository =
        FirebaseAuthRepository(auth, dispatcher)

    @Provides
    fun provideUserRepository(dispatcher: CoroutineDispatcher, dao: UserDao): UserRepository =
        UserRepositoryImpl(dispatcher, dao)
}
