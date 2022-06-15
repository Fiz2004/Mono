package com.fiz.mono.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.fiz.mono.R
import com.fiz.mono.base.android.utils.setVisible
import com.fiz.mono.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val navController: NavController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(com.fiz.mono.navigation.R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTheme()

        setContentView(binding.root)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nav)

        NavigationUI.setupWithNavController(
            bottomNavigation,
            navController
        )

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
        if (AppCompatDelegate.getDefaultNightMode() == viewModel.theme) return

        AppCompatDelegate.setDefaultNightMode(viewModel.theme)
    }
}