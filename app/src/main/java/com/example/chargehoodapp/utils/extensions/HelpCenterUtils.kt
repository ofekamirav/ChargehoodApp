// Loads JSON data into a list of HelpCenterItem objects.

package com.example.chargehoodapp.utils.extensions

import android.content.Context
import android.util.Log
import com.example.chargehoodapp.R
import com.example.chargehoodapp.data.model.HelpCenterItem
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.InputStreamReader

object HelpCenterUtils {
    fun loadHelpCenterData(context: Context): List<HelpCenterItem> {
        return try{
            val inputStream = context.resources.openRawResource(R.raw.help_center_data)
            val reader = InputStreamReader(inputStream)
            val helpCenterType = object : TypeToken<List<HelpCenterItem>>() {}.type
            return Gson().fromJson(reader, helpCenterType)
        }
        catch (e: Exception){
            Log.e("HelpCenterUtils", "Error loading Help Center data", e)
            emptyList()
        }
    }
}