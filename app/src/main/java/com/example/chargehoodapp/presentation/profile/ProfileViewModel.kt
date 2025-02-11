package com.example.chargehoodapp.presentation.profile

import FirebaseModel
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.data.repository.UserRepository
import com.example.chargehoodapp.data.model.User
import com.example.chargehoodapp.data.remote.CloudinaryModel
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileViewModel: ViewModel() {


    private val userRepository = UserRepository()

    private val cloudinaryModel = CloudinaryModel()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    private val _updateStatus = MutableLiveData<String?>()
    val updateStatus: LiveData<String?> get() = _updateStatus

    private val uid = FirebaseModel.getCurrentUser()?.uid

    fun logout() {
        FirebaseModel.logout()
        _currentUser.postValue(null)
        Log.d("TAG", "UserViewModel-User logged out")
    }


    fun getCurrentUser() {
        if (uid != null) {
            viewModelScope.launch {
                try {
                    val user = userRepository.getUserByUid(uid)
                    _currentUser.value = user
                    Log.d("TAG", "UserViewModel-Current user retrieved: ${user?.name}")
                } catch (e: Exception) {
                    Log.e("TAG", "UserViewModel-Error getting current user: ${e.message}")
                    _currentUser.value = null
                }
            }
        } else {
            Log.d("TAG", "UserViewModel-No user is currently logged in")
            _currentUser.value = null
        }
    }

    fun updateUserProfile(name: String?, email: String?, phone: String?, image: Bitmap?, currentPassword: String? = null, newPassword: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (currentPassword != null && (newPassword != null || email != null)) {
                    reauthenticateUser(currentPassword)
                    newPassword?.let { FirebaseModel.updatePassword(it) }
                    email?.let { FirebaseModel.updateEmail(it) }
                }

                val updates = mutableMapOf<String, Any>()
                name?.let { updates["name"] = it }
                phone?.let { updates["phoneNumber"] = it }

                if (image != null) {
                    Log.d("TAG", "ProfileViewModel-Uploading image to Cloudinary")
                    cloudinaryModel.uploadImage(
                        bitmap = image,
                        name = "profile_${System.currentTimeMillis()}",
                        folder = "users",
                        onSuccess = { imageUrl ->
                            if (!imageUrl.isNullOrEmpty()) {
                                updates["profilePictureUrl"] = imageUrl
                            }
                            updateFirestore(updates)
                        },
                        onError = { errorMessage ->
                            handleUpdateResult(false, "Error uploading image: $errorMessage")
                        }
                    )
                } else {
                    updateFirestore(updates)
                }
            } catch (e: Exception) {
                Log.e("TAG", "ProfileViewModel-Error updating profile: ${e.message}")
                handleUpdateResult(false, "Error updating profile: ${e.message}")
            }
        }
    }



    private fun updateFirestore(updates: Map<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = userRepository.updateUser(updates)
                withContext(Dispatchers.Main) {
                    if (success) {
                        _updateStatus.value = "Profile updated successfully."
                    } else {
                        _updateStatus.value = "Failed to update profile."
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "ProfileViewModel-Error updating Firestore: ${e.message}")
                withContext(Dispatchers.Main) {
                    _updateStatus.value = "Error updating Firestore: ${e.message}"
                }
            }
        }
    }


    private fun handleUpdateResult(success: Boolean, message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _updateStatus.value = if (success) {
                message
            } else {
                "Failed: $message"
            }
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.postValue(null)
    }


    suspend fun reauthenticateUser(currentPassword: String) {
        val user = FirebaseModel.getCurrentUser()
        val credential = EmailAuthProvider.getCredential(user?.email ?: "", currentPassword)
        Log.d("TAG", "ProfileViewModel-Email: ${user?.email}")
        try {
            user?.reauthenticate(credential)?.await()
            Log.d("TAG", "ProfileViewModel-Reauthentication successful")
        } catch (e: Exception) {
            Log.e("TAG", "ProfileViewModel-Reauthentication failed: ${e.message}")
            throw e
        }
    }

}



