package com.example.chargehoodapp

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.chargehoodapp.databinding.ActivityMainBinding
import com.example.chargehoodapp.presentation.charging_page.DialogNavigationListener
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), DialogNavigationListener {


    private var binding: ActivityMainBinding?=null
    private var navController: NavController?=null
    private var navHostFragment: NavHostFragment?=null
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null

    override fun onNavigateToChargingPage() {
        val navController = findNavController(R.id.main_nav_host)
        navController.navigate(R.id.action_global_chargingPageFragment)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Set the status bar color
        window.statusBarColor = Color.parseColor("#E8FCF1")

        // Setup the navigation controller
        navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host) as? NavHostFragment
        navController = navHostFragment?.navController

        //Setup the navigation menu
        drawerLayout = binding?.drawerLayout
        navigationView = binding?.navigationView


        setupNavigationMenu()
        handleInitialNavigation()
    }

    private fun setupNavigationMenu() {
        navController?.let { NavigationUI.setupWithNavController(navigationView!!, it) }

        // Lock drawer for certain screens
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment, R.id.welcomeFragment -> {
                    drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                else -> {
                    drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }
        }

        // Handle navigation item clicks
        navigationView?.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_stations -> navController?.navigate(R.id.yourStationListFragment)
                R.id.nav_payments -> navController?.navigate(R.id.paymentMethodFragment)
                R.id.nav_orders -> navController?.navigate(R.id.ordersListFragment)
                R.id.nav_help -> navController?.navigate(R.id.helpCenterFragment)
            }
            drawerLayout?.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun handleInitialNavigation() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)
        val currentUser = FirebaseModel.getCurrentUser()

        when {
            isFirstLaunch -> {
                // Navigate to Welcome screen on first app launch
                navController?.navigate(R.id.welcomeFragment)
                sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
            }
            currentUser != null -> {
                // If user is logged in, navigate to Homepage
                navController?.navigate(R.id.homepageFragment)
            }
            else -> {
                // If user is not logged in, navigate to Login
                navController?.navigate(R.id.loginFragment)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, navController!!) || super.onOptionsItemSelected(item)
    }

}