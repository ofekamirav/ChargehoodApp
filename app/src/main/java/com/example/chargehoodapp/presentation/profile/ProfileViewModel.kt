package com.example.chargehoodapp.presentation.profile

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.data.repository.UserRepository
import com.example.chargehoodapp.data.model.User
import com.example.chargehoodapp.data.remote.CloudinaryModel
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {


    private val userRepository = UserRepository()

    private val cloudinaryModel = CloudinaryModel()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    private val _updateStatus = MutableLiveData<String>()
    val updateStatus: LiveData<String> get() = _updateStatus

    private val uid = FirebaseModel.getCurrentUser()?.uid

    fun logout() {
        FirebaseModel.logout()
        _currentUser.value = null
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


    fun updateUserProfile(
        name: String?,
        email: String?,
        phone: String?,
        password: String?,
        image: Bitmap?
    ) {
        viewModelScope.launch {
            try {
                val updates = mutableMapOf<String, Any>()
                name?.let { updates["name"] = it }
                email?.let { updates["email"] = it }
                phone?.let { updates["phoneNumber"] = it }

                if (image != null) {
                    Log.d("TAG", "ProfileViewModel-Uploading image to Cloudinary")
                    cloudinaryModel.uploadImage(
                        bitmap = image,
                        name = "profile_${System.currentTimeMillis()}",
                        folder = "users",
                        onSuccess = { imageUrl ->
                            Log.d("TAG", "ProfileViewModel-Image uploaded successfully. URL: $imageUrl")
                            if (imageUrl?.isNotEmpty() == true) {
                                updates["profilePictureUrl"] = imageUrl
                                updateFirestore(updates, email, password)
                            }
                        },
                        onError = { errorMessage ->
                            Log.e("TAG", "ProfileViewModel-Error uploading image: $errorMessage")
                            _updateStatus.postValue("Error uploading image: $errorMessage")
                        }
                    )
                } else {
                    val currentUser = _currentUser.value
                    currentUser?.profilePictureUrl?.let {
                        updates["profilePictureUrl"] = it
                        Log.d("TAG", "ProfileViewModel-Using existing image URL: $it")
                    }
                    updateFirestore(updates, email, password)
                }
            } catch (e: Exception) {
                Log.e("TAG", "ProfileViewModel-Error updating profile: ${e.message}")
                _updateStatus.postValue("Error updating profile: ${e.message}")
            }
        }
    }

    private fun updateFirestore(updates: Map<String, Any>, email: String?, password: String?) {
        viewModelScope.launch {
            try {
                //Update email and password in Firebase Authentication if provided
                email?.let { FirebaseModel.updateEmail(it) }
                password?.let { FirebaseModel.updatePassword(it) }

                //Update in users collection in Firestore
                val success = userRepository.updateUser(updates)
                Log.d("TAG", "UserViewModel-Firestore update successful: $success")
                _updateStatus.value = if (success) {
                    "Profile updated successfully."
                } else {
                    "Failed to update profile."
                }
            } catch (e: Exception) {
                Log.e("TAG", "ProfileViewModel-Error updating Firestore: ${e.message}")
                _updateStatus.value = "Error updating Firestore: ${e.message}"
            }
        }
    }

}
