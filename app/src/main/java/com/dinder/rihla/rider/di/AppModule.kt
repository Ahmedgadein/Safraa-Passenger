package com.dinder.rihla.rider.di

import com.dinder.rihla.rider.data.remote.auth.AuthRepository
import com.dinder.rihla.rider.data.remote.auth.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@ExperimentalCoroutinesApi
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(auth: FirebaseAuth, dispatcher: CoroutineDispatcher):
        AuthRepository =
        FirebaseAuthRepository(auth, dispatcher)
}
