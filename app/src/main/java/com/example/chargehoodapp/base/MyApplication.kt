package com.example.chargehoodapp.base

import android.app.Application
import android.content.Context


//Shared preferences to all project
class MyApplication: Application() {
    object Globals {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.context = applicationContext
    }
}