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
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume


class CloudinaryModel {

    private var cloudinaryInitialized = false

    suspend fun uploadImage(
        bitmap: Bitmap,
        name: String,
        folder: String,
        onSuccess: StringCallback,
        onError: StringCallback
    ): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val context = MyApplication.Globals.context
            if (context == null) {
                Log.e("Cloudinary", "Context is null. Cannot upload image.")
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }

            if (!cloudinaryInitialized) {
                val config = mapOf(
                    "cloud_name" to CLOUD_NAME,
                    "api_key" to API_KEY,
                    "api_secret" to API_SECRET
                )
                MediaManager.init(context, config)
                MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.defaultPolicy()
                cloudinaryInitialized = true
            }

            val file: File = bitmap.toFile(context, name)
            Log.d("Cloudinary", "Starting upload to Cloudinary. File path: ${file.path}")

            MediaManager.get().upload(file.path)
                .option("folder", folder)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        if (url != null) {
                            Log.d("Cloudinary", "Upload successful. URL: $url")
                            onSuccess(url)
                            continuation.resume(true) // ✅ חזרנו TRUE אם ההעלאה הצליחה
                        } else {
                            Log.e("Cloudinary", "Upload failed: No URL returned")
                            onError("Upload failed: No URL returned")
                            continuation.resume(false)
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.e("Cloudinary", "Upload failed. Error: ${error?.description}")
                        onError(error?.description ?: "Unknown error")
                        continuation.resume(false)
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                })
                .dispatch()
        }
    }

}
