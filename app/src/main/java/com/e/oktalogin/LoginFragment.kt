package com.e.oktalogin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.e.oktalogin.BuildConfig.ORG_URL
import com.e.oktalogin.databinding.FragmentLoginBinding
import com.okta.authn.sdk.client.AuthenticationClient
import com.okta.authn.sdk.client.AuthenticationClients
import com.okta.authn.sdk.resource.AuthenticationStatus
import com.okta.oidc.OIDCConfig
import com.okta.oidc.Okta
import com.okta.oidc.RequestCallback
import com.okta.oidc.clients.AuthClient
import com.okta.oidc.results.Result
import com.okta.oidc.storage.SharedPreferenceStorage
import com.okta.oidc.util.AuthorizationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates.notNull

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authClient: AuthClient
    private var authenticationClient: AuthenticationClient? = null
    private var hardwareKeystore: Boolean = false

    private var config: OIDCConfig by notNull()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createAuthClient()

        binding.loginButton.setOnClickListener {
            val user = binding.userField.text.toString()
            val password = binding.passwordField.text.toString()

            validateFields(user, password)
        }

        if (authClient.sessionClient.isAuthenticated) {
            userAuthenticated()
        }

        binding.linkForgotPassword.setOnClickListener {

        }

    }


    private fun createAuthClient() {
        config = OIDCConfig.Builder()
            .withJsonFile(requireActivity(), R.raw.config)
            .create()

        authClient = Okta.AuthBuilder()
            .withConfig(config)
            .withContext(requireActivity())
            .withStorage(SharedPreferenceStorage(requireActivity(), PREF_STORAGE_AUTH))
            .setRequireHardwareBackedKeyStore(hardwareKeystore)
            .create()

        authenticationClient = AuthenticationClients.builder()
            .setOrgUrl(ORG_URL)
            .build()

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
        binding.inProgress.show()
        lifecycleScope.launch(Dispatchers.Main) {
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
                    binding.inProgress.hide()
                }
            }
        }
    }

    private fun singInWhitOkta(sessionToken: String) {
        lifecycleScope.launch {
            authClient.signIn(sessionToken, null, object :
                RequestCallback<Result, AuthorizationException> {

                override fun onSuccess(result: Result) {
                    userAuthenticated()
                }

                override fun onError(error: String?, exception: AuthorizationException?) {
                    binding.inProgress.hide()
                    Log.d("Error", error ?: exception?.localizedMessage.toString())
                }

            })
        }
    }

    private fun userAuthenticated() {
        findNavController().navigate(LoginFragmentDirections.actionToHomeFragment())
    }

    companion object {
        const val PREF_STORAGE_AUTH: String = "auth_client"
    }
}