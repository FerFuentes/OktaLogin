package com.e.oktalogin.ui.home

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.e.oktalogin.managers.ApplicationManager
import com.e.oktalogin.managers.authentication.AuthenticationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager,
    private val applicationManager: ApplicationManager
) : ViewModel() {

    val authenticationState = applicationManager.authenticationState.asLiveData(
        viewModelScope.coroutineContext
    )

    fun signOut() {
        authenticationManager.signOutOkta()
    }

    fun sigOutAuth(context: FragmentActivity){
        authenticationManager.signOutAuth0(context)
    }
}