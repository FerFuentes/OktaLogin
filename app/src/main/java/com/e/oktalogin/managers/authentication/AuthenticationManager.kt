package com.e.oktalogin.managers.authentication

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.e.oktalogin.R
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

    private val account = Auth0(
        "95ydIwyYuy6Gqj70mSDhfd1qsKiof8PV",
        "authenticationtestmovil.us.auth0.com"
    )

    fun authenticateUser(context: FragmentActivity? = null, type: Int, user: String, password: String) {
        updateAuthenticationStates(AuthenticationState.LOADING)

        when(type){
            R.id.radio_button_okta -> oktaAuthentication(user, password)
            R.id.radio_button_aut0 -> context?.let { auth0Authentication(it) }
        }

    }

    private fun auth0Authentication(context: FragmentActivity) {

        WebAuthProvider.login(account)
            .withScheme("demo")
            .withScope("openid profile email")
            .start(context, object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    updateAuthenticationStates(AuthenticationState.ERROR_ON_CREDENTIALS)
                }

                override fun onSuccess(result: Credentials) {
                    updateAuthenticationStates(AuthenticationState.LOGIN_SUCCESS)
                }
            } )
    }

    private fun oktaAuthentication(user: String, password: String){
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

    fun signOutOkta() {
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

    fun signOutAuth0(context: FragmentActivity) {
        WebAuthProvider.logout(account)
            .withScheme("demo")
            .start(context, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(payload: Void?) {
                    updateAuthenticationStates(AuthenticationState.LOGOUT_SUCCESS)
                }

                override fun onFailure(exception: AuthenticationException) {

                }
            })
    }
}