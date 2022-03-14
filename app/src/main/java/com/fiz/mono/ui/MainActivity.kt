package com.fiz.mono.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.fiz.mono.R
import com.fiz.mono.databinding.ActivityMainBinding
import com.fiz.mono.ui.on_boarding.OnBoardingViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val viewModel: OnBoardingViewModel by viewModels()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigation = binding.bottomNav
        bottomNavigation.setupWithNavController(navController)

        val inputMenuItem: MenuItem = bottomNavigation.menu.findItem(R.id.inputFragment)
        val calculatorMenuItem: MenuItem = bottomNavigation.menu.findItem(R.id.calculatorFragment)
        val reportMenuItem: MenuItem = bottomNavigation.menu.findItem(R.id.reportFragment)
        val settingsMenuItem: MenuItem = bottomNavigation.menu.findItem(R.id.settingsFragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.inputFragment ||
                destination.id == R.id.calculatorFragment ||
                destination.id == R.id.reportFragment ||
                destination.id == R.id.settingsFragment
            ) {
                bottomNavigation.visibility = View.VISIBLE
            } else {
                bottomNavigation.visibility = View.GONE
            }
        }


        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inputFragment -> {
                    inputMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.input_page_selected
                    )
                    calculatorMenuItem.icon =
                        AppCompatResources.getDrawable(this, R.drawable.calculator_page_unselected)
                    reportMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.report_page_unselected
                    )
                    settingsMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.settings_page_unselected
                    )

                }
                R.id.calculatorFragment -> {
                    inputMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.input_page_unselected
                    )
                    calculatorMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.calculator_page_selected
                    )
                    reportMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.report_page_unselected
                    )
                    settingsMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.settings_page_unselected
                    )

                }
                R.id.reportFragment -> {
                    inputMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.input_page_unselected
                    )
                    calculatorMenuItem.icon =
                        AppCompatResources.getDrawable(this, R.drawable.calculator_page_unselected)
                    reportMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.report_page_selected
                    )
                    settingsMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.settings_page_unselected
                    )

                }
                R.id.settingsFragment -> {
                    inputMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.input_page_unselected
                    )
                    calculatorMenuItem.icon =
                        AppCompatResources.getDrawable(this, R.drawable.calculator_page_unselected)
                    reportMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.report_page_unselected
                    )
                    settingsMenuItem.icon = AppCompatResources.getDrawable(
                        this,
                        R.drawable.settings_page_selected
                    )

                }
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
            item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
            true
        }
    }
}