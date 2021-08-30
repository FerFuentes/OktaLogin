package com.e.oktalogin.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.e.oktalogin.databinding.FragmentHomeBinding
import com.e.oktalogin.managers.authentication.AuthenticationState
import com.e.oktalogin.ui.login.LoginFragmentDirections
import com.e.oktalogin.ui.login.LoginViewModel
import com.okta.oidc.clients.sessions.SessionClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.authenticationState.observe(viewLifecycleOwner, {
            when(it){
                AuthenticationState.LOGOUT_SUCCESS -> {userSingOut()}
                else -> {}
            }
        })

        binding.logoutButton.setOnClickListener {
            homeViewModel.signOut()
        }
    }

    private fun userSingOut() {
        findNavController().navigate(HomeFragmentDirections.actionToLoginFragment())
    }
}