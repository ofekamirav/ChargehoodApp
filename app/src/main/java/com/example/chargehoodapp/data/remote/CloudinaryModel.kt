package com.example.chargehoodapp.data.remote

import android.graphics.Bitmap
import android.util.Log
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

    private var cloudinaryInitialized = false

    fun uploadImage(
        bitmap: Bitmap,
        name: String,
        folder: String,
        onSuccess: StringCallback,
        onError: StringCallback
    ) {
        //Make sure Cloudinary is initialized just once
        if (!cloudinaryInitialized) {
            val context = MyApplication.Globals.context
            if (context == null) {
                Log.e("TAG", "Context is null. Cannot upload image.")
                return
            }

            val config = mapOf(
                "cloud_name" to CLOUD_NAME,
                "api_key" to API_KEY,
                "api_secret" to API_SECRET
            )

            MediaManager.init(context, config)
            MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.defaultPolicy()

            cloudinaryInitialized = true

            //Starting the upload logic from here
            if (context == null) {
                Log.e("TAG", "Context is null. Cannot upload image.")
                return
            }
            val file: File = bitmap.toFile(context, name)

            Log.d("TAG", "Starting upload to Cloudinary. File path: ${file.path}")
            MediaManager.get().upload(file.path)
                .option("folder", folder)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Log.d("TAG", "Cloudinary upload started. Request ID: $requestId")
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        Log.d("TAG", "Cloudinary upload progress: $bytes / $totalBytes")
                    }

                    override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String ?: ""
                        Log.d("TAG", "Cloudinary upload successful. URL: $url")
                        onSuccess(url)
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.e("TAG", "Cloudinary upload failed. Error: ${error?.description}")
                        onError(error?.description ?: "Unknown error")
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        Log.w("TAG", "Cloudinary upload rescheduled. Error: ${error?.description}")
                    }
                })
                .dispatch()
        }

    }
}
