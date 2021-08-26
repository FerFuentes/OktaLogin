package com.e.oktalogin

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.e.oktalogin.databinding.ActivityMainBinding
import com.e.oktalogin.ui.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigationComponent()


    }

    private fun setupNavigationComponent() {
        navController = (
                supportFragmentManager.findFragmentById(binding.navHostContainer.id) as NavHostFragment
                ).navController
    }
}