package com.e.oktalogin.ui.login

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.e.oktalogin.R
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

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val id = checkedId
        }

        binding.loginButton.setOnClickListener {
            val user = binding.userField.text.toString()
            val password = binding.passwordField.text.toString()

            validateFields(user, password)
        }

        if (loginViewModel.userIsAuthenticated()) {
            userAuthenticated()
        }

        loginViewModel.authenticationState.observe(viewLifecycleOwner, {
            when (it) {
                AuthenticationState.LOADING -> binding.inProgress.show()
                AuthenticationState.LOGIN_SUCCESS -> userAuthenticated()
                AuthenticationState.ERROR_ON_CREDENTIALS -> showMessage(null, "Error Credentials")
                else -> {
                }
            }
        })

        binding.linkForgotPassword.setOnClickListener {

        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            updateUI(checkedId)
        }

    }

    private fun updateUI(type: Int){
        when (type) {
            R.id.radio_button_aut0 -> {
                binding.userField.visibility = View.GONE
                binding.passwordField.visibility = View.GONE
                binding.loginButton.text = "Authenticate"
            }
            R.id.radio_button_okta ->  {
                binding.userField.visibility = View.VISIBLE
                binding.passwordField.visibility = View.VISIBLE
                binding.loginButton.setText(R.string.login)
            }
        }
    }

    private fun validateFields(user: String, password: String) {
        user.isEmpty().or(password.isEmpty()).run {
            if (this) {
                when (binding.radioGroup.checkedRadioButtonId) {
                    R.id.radio_button_aut0 -> authenticateUser(user, password)

                    R.id.radio_button_okta -> showMessage(
                        null,
                        "Your user and password are needed "
                    )
                }

            } else {
                authenticateUser(user, password)
            }
        }
    }

    private fun showMessage(title: String?, message: String) {
        binding.inProgress.hide()
        AlertDialog.Builder(requireActivity())
            .setCancelable(false)
            .setTitle(R.string.login)
            .setMessage(message)
            .setPositiveButton("ok") { _, _ ->

            }
            .show()
    }

    private fun authenticateUser(user: String, password: String) {
        loginViewModel.authenticateUser(requireActivity(),binding.radioGroup.checkedRadioButtonId, user, password)
    }


    private fun userAuthenticated() {
        findNavController().navigate(LoginFragmentDirections.actionToHomeFragment())
    }

}