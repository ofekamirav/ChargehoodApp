package com.example.chargehoodapp.data.remote

import android.graphics.Bitmap
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.policy.GlobalUploadPolicy
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.base.StringCallback
import com.example.chargehoodapp.BuildConfig.CLOUD_NAME
import com.example.chargehoodapp.BuildConfig.API_KEY
import com.example.chargehoodapp.BuildConfig.API_SECRET


import com.example.chargehoodapp.utils.extensions.toFile
import java.io.File

class CloudinaryModel {

    init{
        val config = mapOf(
            "cloud_name" to CLOUD_NAME,
            "api_key" to API_KEY,
            "api_secret" to API_SECRET
        )

        MyApplication.Globals.context?.let{
            MediaManager.init(it, config)
            MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.defaultPolicy()
        }

    }

    fun uploadImage(
        bitmap: Bitmap,
        name: String,
        onSuccess: StringCallback,
        onError: StringCallback
    ) {
        val context = MyApplication.Globals.context ?: return
        val file: File = bitmap.toFile(context, name)

        MediaManager.get().upload(file.path)
            .option("folder", "images")
            .callback(object  : UploadCallback {
                override fun onStart(requestId: String?) {

                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {

                }

                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String ?: ""
                    onSuccess(url)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError(error?.description ?: "Unknown error")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {

                }

            })
            .dispatch()
    }

}