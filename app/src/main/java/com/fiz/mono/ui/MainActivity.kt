package com.fiz.mono.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.fiz.mono.R
import com.fiz.mono.core.util.setVisible
import com.fiz.mono.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupBottomNavigation()
        setupTheme()
    }

    private fun setupBottomNavigation() {
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigation = binding.bottomNav
        bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigation.setVisible(
                destination.id == R.id.inputFragment ||
                        destination.id == R.id.calculatorFragment ||
                        destination.id == R.id.reportFragment ||
                        destination.id == R.id.settingsFragment
            )
        }
    }

    private fun setupTheme() {
        val mode = viewModel.getMode()
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}