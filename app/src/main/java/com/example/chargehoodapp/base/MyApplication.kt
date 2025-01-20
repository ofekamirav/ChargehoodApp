package com.example.studentsapp.base

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp


//Shared preferences to all project
class MyApplication: Application() {
    object Globals {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.context = applicationContext
        FirebaseApp.initializeApp(this)
    }
}