package com.fiz.mono.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.ActivityMainBinding
import com.fiz.mono.util.setVisible

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val mainPreferencesViewModel: MainPreferencesViewModel by viewModels {
        MainPreferencesViewModelFactory(
            getSharedPreferences(
                getString(R.string.preferences),
                MODE_PRIVATE
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        if (mainPreferencesViewModel.themeLight.value == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}