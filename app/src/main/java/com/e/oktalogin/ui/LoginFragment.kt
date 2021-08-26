package com.e.oktalogin.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.e.oktalogin.databinding.FragmentLoginBinding
import com.e.oktalogin.managers.authentication.AuthenticationState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val user = binding.userField.text.toString()
            val password = binding.passwordField.text.toString()

            validateFields(user, password)
        }

        loginViewModel.authenticationState.observe(viewLifecycleOwner, {
            when(it){
                AuthenticationState.LOADING -> binding.inProgress.show()
                AuthenticationState.HOME -> userAuthenticated()
                else -> {}
            }
        })

/*        if (authClient.sessionClient.isAuthenticated) {
            userAuthenticated()
        }*/

        binding.linkForgotPassword.setOnClickListener {

        }

    }


    private fun validateFields(user: String, password: String) {
        user.isEmpty().or(password.isEmpty()).run {
            if (this) {

            } else {
                authenticateUser(user, password)
            }
        }
    }

    private fun authenticateUser(user: String, password: String) {
        loginViewModel.authenticateUser(user, password)
    }



    private fun userAuthenticated() {
        findNavController().navigate(LoginFragmentDirections.actionToHomeFragment())
    }

    companion object {
        const val PREF_STORAGE_AUTH: String = "auth_client"
    }
}