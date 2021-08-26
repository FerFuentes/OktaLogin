package com.e.oktalogin.managers.authentication

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AuthenticationEventBus {

    private val _stateAuthentication = MutableSharedFlow<AuthenticationState>()
    val stateAuthentication: SharedFlow<AuthenticationState> = _stateAuthentication

    suspend fun updateAuthenticationStates(state: AuthenticationState) {
        _stateAuthentication.emit(state)
    }

}