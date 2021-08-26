package com.e.oktalogin.di.module

import android.content.Context
import com.e.oktalogin.BuildConfig
import com.e.oktalogin.R
import com.e.oktalogin.di.ApplicationCoroutineScope
import com.e.oktalogin.managers.authentication.AuthenticationEventBus
import com.e.oktalogin.managers.authentication.AuthenticationManager
import com.okta.authn.sdk.client.AuthenticationClient
import com.okta.authn.sdk.client.AuthenticationClients
import com.okta.oidc.OIDCConfig
import com.okta.oidc.Okta
import com.okta.oidc.clients.AuthClient
import com.okta.oidc.storage.SharedPreferenceStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    @Provides
    @Singleton
    fun provideAuthConfiguration(
        @ApplicationContext context: Context,
        @ApplicationCoroutineScope externalScope: CoroutineScope,
        authenticationEventBus: AuthenticationEventBus,
        authClient: AuthClient,
        authenticationClient: AuthenticationClient,
        config: OIDCConfig
    ): AuthenticationManager = AuthenticationManager(
        context,
        externalScope,
        authenticationEventBus,
        authClient,
        authenticationClient,
        config
    )

    @Provides
    @Singleton
    fun provideAuthClientConfiguration(
        @ApplicationContext context: Context,
    ): OIDCConfig = OIDCConfig.Builder()
        .withJsonFile(context, R.raw.config)
        .create()

    @Provides
    @Singleton
    fun provideAuthBuilder(
        config: OIDCConfig,
        @ApplicationContext context: Context,
    ): AuthClient = Okta.AuthBuilder()
        .withConfig(config)
        .withContext(context)
        .withStorage(SharedPreferenceStorage(context, "auth_client"))
        .setRequireHardwareBackedKeyStore(false)
        .create()

    @Provides
    @Singleton
    fun provideAuthClient(): AuthenticationClient = AuthenticationClients.builder()
        .setOrgUrl(BuildConfig.ORG_URL)
        .build()

    @Provides
    @Singleton
    fun provideAuthEventBus(): AuthenticationEventBus {
        return AuthenticationEventBus()
    }
}