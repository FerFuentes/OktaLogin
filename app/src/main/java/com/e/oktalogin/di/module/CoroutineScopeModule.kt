package com.e.oktalogin.di.module

import com.e.oktalogin.di.ApplicationCoroutineScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {

    @Provides
    @Singleton
    @ApplicationCoroutineScope
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Job() + Dispatchers.Default)
    }
}