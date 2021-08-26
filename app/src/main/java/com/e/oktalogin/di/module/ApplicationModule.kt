package com.e.oktalogin.di.module

import com.e.oktalogin.managers.ApplicationManager
import com.e.oktalogin.managers.authentication.AuthenticationEventBus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideApplicationManager(
        authenticationEventBus: AuthenticationEventBus
    ) = ApplicationManager(authenticationEventBus)
}