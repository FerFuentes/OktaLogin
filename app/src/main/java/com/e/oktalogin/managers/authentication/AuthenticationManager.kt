package com.e.oktalogin.managers.authentication

import android.content.Context
import android.util.Log
import com.e.oktalogin.di.ApplicationCoroutineScope
import com.okta.authn.sdk.client.AuthenticationClient
import com.okta.authn.sdk.resource.AuthenticationStatus
import com.okta.oidc.OIDCConfig
import com.okta.oidc.RequestCallback
import com.okta.oidc.clients.AuthClient
import com.okta.oidc.results.Result
import com.okta.oidc.util.AuthorizationException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthenticationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationCoroutineScope private val externalScope: CoroutineScope,
    private val authenticationEventBus: AuthenticationEventBus,
    private var authClient: AuthClient,
    private var authenticationClient: AuthenticationClient,
    private var config: OIDCConfig
) {

    init {

        externalScope.launch {
            authenticationEventBus.stateAuthentication
                .catch { }
                .collect {
                    when (it) {
                        AuthenticationState.LOGIN -> {
                        }
                        AuthenticationState.LOGOUT -> {
                        }
                    }
                }
        }
    }


    fun authenticateUser(user: String, password: String) {
        updateAuthenticationStates(AuthenticationState.LOADING)

        externalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                runCatching {
                    authenticationClient?.authenticate(
                        user,
                        password.toCharArray(),
                        null,
                        null
                    )
                }.onSuccess {
                    if (!it.isNullOrEmpty()) {
                        when (it.status) {
                            AuthenticationStatus.SUCCESS -> singInWhitOkta(sessionToken = it.sessionToken)
                            else -> {

                            }
                        }
                    }

                }.onFailure {
                    Log.d("error", it.localizedMessage ?: "")
                }
            }
        }
    }

    private fun singInWhitOkta(sessionToken: String) {
        externalScope.launch {
            authClient.signIn(sessionToken, null, object :
                RequestCallback<Result, AuthorizationException> {

                override fun onSuccess(result: Result) {
                    Log.d("Login", "Success")
                    updateAuthenticationStates(AuthenticationState.HOME)
                }

                override fun onError(error: String?, exception: AuthorizationException?) {
                    Log.d("Error", error ?: exception?.localizedMessage.toString())
                }

            })
        }
    }

    private fun updateAuthenticationStates(state: AuthenticationState) {
        externalScope.launch {
            authenticationEventBus.updateAuthenticationStates(state)
        }
    }
}