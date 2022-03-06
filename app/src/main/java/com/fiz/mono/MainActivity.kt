package com.fiz.mono

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.MenuItemCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.fiz.mono.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController


        val bottomNavigation=findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigation.setupWithNavController(navController)

        val inputMenuItem:MenuItem=bottomNavigation.menu.findItem(R.id.inputFragment)
        val calculatorMenuItem:MenuItem=bottomNavigation.menu.findItem(R.id.calculatorFragment)
        val reportMenuItem:MenuItem=bottomNavigation.menu.findItem(R.id.reportFragment)
        val settingsMenuItem:MenuItem=bottomNavigation.menu.findItem(R.id.settingsFragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.onBoardingFragment ||
                destination.id == R.id.PINPasswordFragment    ) {
                bottomNavigation.visibility = View.GONE
            } else {
                bottomNavigation.visibility = View.VISIBLE
            }
        }


        bottomNavigation.setOnItemSelectedListener { item->
            when (item.itemId){
                R.id.inputFragment->{
                    inputMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.input_page_selected)
                    calculatorMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.calculator_page_unselected)
                    reportMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.report_page_unselected)
                    settingsMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.settings_page_unselected)
//                    supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, InputFragment()).commit()

                }
                R.id.calculatorFragment->{
                    inputMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.input_page_unselected)
                    calculatorMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.calculator_page_selected)
                    reportMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.report_page_unselected)
                    settingsMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.settings_page_unselected)
//                    supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, CalculatorFragment()).commit()

                }
                R.id.reportFragment->{
                    inputMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.input_page_unselected)
                    calculatorMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.calculator_page_unselected)
                    reportMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.report_page_selected)
                    settingsMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.settings_page_unselected)
//                    supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, ReportFragment()).commit()

                }
                R.id.settingsFragment->{
                    inputMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.input_page_unselected)
                    calculatorMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.calculator_page_unselected)
                    reportMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.report_page_unselected)
                    settingsMenuItem.icon=AppCompatResources.getDrawable(this,R.drawable.settings_page_selected)
//                    supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, SettingsFragment()).commit()

                }
                else->{
                    return@setOnItemSelectedListener false
                }
            }
            item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
            true
        }
    }
}