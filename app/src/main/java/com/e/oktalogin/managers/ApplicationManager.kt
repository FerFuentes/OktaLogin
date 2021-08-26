package com.e.oktalogin.managers

import com.e.oktalogin.managers.authentication.AuthenticationEventBus
import com.e.oktalogin.managers.authentication.AuthenticationState
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ApplicationManager @Inject constructor(
    private val authenticationEventBus: AuthenticationEventBus
){
    val authenticationState:  SharedFlow<AuthenticationState> = authenticationEventBus.stateAuthentication

    suspend fun updateAuthenticationState(state: AuthenticationState) = authenticationEventBus.updateAuthenticationStates(state)
}