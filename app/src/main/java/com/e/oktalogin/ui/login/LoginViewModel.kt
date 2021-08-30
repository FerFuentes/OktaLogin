package com.e.oktalogin.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.e.oktalogin.managers.ApplicationManager
import com.e.oktalogin.managers.authentication.AuthenticationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager,
    private val applicationManager: ApplicationManager
): ViewModel(){

    val authenticationState = applicationManager.authenticationState
        .asLiveData(viewModelScope.coroutineContext)

    fun authenticateUser(user: String, password: String){
        authenticationManager.authenticateUser(user, password)
    }

    fun userIsAuthenticated() = authenticationManager.userIsAuthenticated()

}