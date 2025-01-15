package com.example.chargehoodapp

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
    //var sharedPreferences: SharedPreferences?=null
    private var navController: NavController?=null
    private var navHostFragment: NavHostFragment?=null
    private var drawerLayout: DrawerLayout? = null
    private var navigation_view: NavigationView? = null


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

        // Setup the navigation controller
        navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host) as? NavHostFragment
        navController = navHostFragment?.navController
        setSupportActionBar(binding?.toolbar)
        drawerLayout = binding?.drawerLayout
        navigation_view = binding?.navigationView
        navController?.let {
            NavigationUI.setupActionBarWithNavController(this, it, binding?.drawerLayout)
            NavigationUI.setupWithNavController(navigation_view!!, navController!!)
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

//
//        navController?.addOnDestinationChangedListener { _, destination, _ ->
//            if (destination.id == R.id.loginFragment || destination.id == R.id.registerFragment || destination.id == R.id.welcomeFragment) {
//                hideToolbar()
//            } else {
//                showToolbar()
//            }
//        }


            //navController?.navigate(R.id.welcomeFragment)

            navController?.navigate(R.id.homepageFragment)
//        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
//        val isFirstLaunch = sharedPreferences?.getBoolean("isFirstLaunch", true)
//
//        if (isFirstLaunch == true) {
//        //Navigate to WelcomeFragment
//            navController?.navigate(R.id.welcomeFragment)
//
//            sharedPreferences?.edit()?.putBoolean("isFirstLaunch", false)?.apply()
//        } else {
//            navController?.navigate(R.id.loginFragment)
//        }


    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.navigateUp(binding?.drawerLayout) ?: super.onSupportNavigateUp()
    }
}