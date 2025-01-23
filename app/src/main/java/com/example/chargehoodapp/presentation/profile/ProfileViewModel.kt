package com.example.chargehoodapp.presentation.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.data.repository.UserRepository
import com.example.chargehoodapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {


    private val userRepository = UserRepository()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> get() = _loginError

    private val _loginSuccess = MutableLiveData<String?>()
    val loginSuccess: LiveData<String?> get() = _loginSuccess

    private val auth = FirebaseAuth.getInstance()



    fun logout() {
        auth.signOut()
        _currentUser.value = null
        Log.d("TAG", "UserViewModel-User logged out")
    }



    fun getCurrentUser() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                try {
                    val user = userRepository.getUserByUid(uid)
                    _currentUser.value = user
                    Log.d("TAG", "UserViewModel-Current user retrieved: ${user?.name}")
                } catch (e: Exception) {
                    Log.e("TAG", "UserViewModel-Error getting current user: ${e.message}")
                    _loginError.value = "Error getting current user: ${e.message}"
                    _currentUser.value = null
                }
            }
        } else {
            Log.d("TAG", "UserViewModel-No user is currently logged in")
            _currentUser.value = null
        }
    }


    fun updateUser(name: String, email: String, phone: String, password: String) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                try {
                    val updates = mutableMapOf<String, Any>()
                    if (name.isNotEmpty()) {
                        updates["name"] = name
                    }
                    if (email.isNotEmpty()) {
                        updates["email"] = email
                    }
                    if (phone.isNotEmpty()) {
                        updates["phoneNumber"] = phone
                    }
                    if (password.isNotEmpty()) {
                        updates["password"] = password
                    }
                    //adding the photo url if it exists



                } catch (e: Exception) {
                    Log.e("TAG", "UserViewModel-Error updating user: ${e.message}")
                }


            }
        }
    }
}