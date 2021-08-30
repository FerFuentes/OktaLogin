package com.e.oktalogin.managers.authentication

import android.content.Context
import com.e.oktalogin.di.ApplicationCoroutineScope
import com.okta.authn.sdk.client.AuthenticationClient
import com.okta.authn.sdk.resource.AuthenticationStatus
import com.okta.oidc.OIDCConfig
import com.okta.oidc.RequestCallback
import com.okta.oidc.ResultCallback
import com.okta.oidc.clients.AuthClient
import com.okta.oidc.results.Result
import com.okta.oidc.util.AuthorizationException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    fun authenticateUser(user: String, password: String) {
        updateAuthenticationStates(AuthenticationState.LOADING)

        externalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                runCatching {
                    authenticationClient.authenticate(
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
                    updateAuthenticationStates(AuthenticationState.ERROR_ON_CREDENTIALS)
                }
            }
        }
    }

    private fun singInWhitOkta(sessionToken: String) {
        externalScope.launch {
            authClient.signIn(sessionToken, null, object :
                RequestCallback<Result, AuthorizationException> {

                override fun onSuccess(result: Result) {
                    updateAuthenticationStates(AuthenticationState.LOGIN_SUCCESS)
                }

                override fun onError(error: String?, exception: AuthorizationException?) {

                }

            })
        }
    }

    private fun updateAuthenticationStates(state: AuthenticationState) {
        externalScope.launch {
            authenticationEventBus.updateAuthenticationStates(state)
        }
    }

    fun userIsAuthenticated() = authClient.sessionClient.isAuthenticated

    fun signOut() {
        updateAuthenticationStates(AuthenticationState.LOADING)
        authClient.signOut(object :
            ResultCallback<Int, AuthorizationException> {
            override fun onSuccess(result: Int) {
                updateAuthenticationStates(AuthenticationState.LOGOUT_SUCCESS)
            }

            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(msg: String?, exception: AuthorizationException?) {
                TODO("Not yet implemented")
            }
        })
    }
}