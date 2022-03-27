package com.fiz.mono.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.ActivityMainBinding
import com.fiz.mono.util.setVisible

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPreferences()

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigation = binding.bottomNav
        bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigation.setVisible(destination.id == R.id.inputFragment ||
                    destination.id == R.id.calculatorFragment ||
                    destination.id == R.id.reportFragment ||
                    destination.id == R.id.settingsFragment)
        }
    }

    private fun loadPreferences() {
        val sharedPreferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE)
        mainViewModel.firstTime = sharedPreferences.getBoolean("firstTime", true)
        val loadCurrency = sharedPreferences.getString("currency", "$") ?: "$"
        mainViewModel.setCurrency(loadCurrency)
        mainViewModel.PIN = sharedPreferences.getString("PIN", "") ?: ""
    }
}