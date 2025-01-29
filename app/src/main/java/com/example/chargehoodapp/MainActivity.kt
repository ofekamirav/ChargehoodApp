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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.chargehoodapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {


    private var binding: ActivityMainBinding?=null
    private var navController: NavController?=null
    private var navHostFragment: NavHostFragment?=null
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null


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

        navController?.let {
            NavigationUI.setupWithNavController(navigationView!!, navController!!)
        }


        //Show the navigation drawer when the hamburger icon is clicked
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

            navController?.navigate(R.id.welcomeFragment)

        navigationView?.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_stations -> {
                    navController?.navigate(R.id.yourStationListFragment)
                }
                // Add other menu items here
            }
            drawerLayout?.closeDrawer(GravityCompat.START) // Close drawer after selection
            true
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, navController!!) || super.onOptionsItemSelected(item)
    }

}