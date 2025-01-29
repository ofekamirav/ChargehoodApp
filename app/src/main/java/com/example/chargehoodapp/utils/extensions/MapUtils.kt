package com.example.chargehoodapp.utils.extensions

import com.example.chargehoodapp.BuildConfig

//Set up the static map URL
object MapUtils {
    fun getStaticMapUrl(latitude: Double, longitude: Double): String {
        val apiKey = BuildConfig.GOOGLE_MAP_API_KEY
        return "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=$latitude,$longitude" +
                "&zoom=15&size=600x300&maptype=roadmap" +
                "&markers=color:#77BFBE|$latitude,$longitude" +
                "&key=$apiKey"
    }
}