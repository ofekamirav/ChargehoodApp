package com.example.chargehoodapp

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.data.User
import com.example.chargehoodapp.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel: ViewModel() {

    private val userRepository = UserRepository()

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> get() = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> get() = _passwordError

    private val _phoneError = MutableLiveData<String?>()
    val phoneError: LiveData<String?> get() = _phoneError

    private val _nameError = MutableLiveData<String?>()
    val nameError: LiveData<String?> get() = _nameError

    private val _registrationSuccess = MutableLiveData<String?>()
    val registrationSuccess: LiveData<String?> get() = _registrationSuccess

    private val _registrationError = MutableLiveData<String?>()
    val registrationError: LiveData<String?> get() = _registrationError

    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> get() = _loginError

    private val _loginSuccess = MutableLiveData<String?>()
    val loginSuccess: LiveData<String?> get() = _loginSuccess

    private val auth = FirebaseAuth.getInstance()

//Registration--------------------------------------------------------------------------------------------------
    fun validateAndRegisterUser(
        email: String,
        password: String,
        phoneNumber: String,
        fullName: String
    ) {
        var isValid = true

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Invalid email format"
            isValid = false
        } else {
            _emailError.value = null
        }

        if (password.length < 6) {
            _passwordError.value = "Password must be at least 6 characters"
            isValid = false
        } else {
            _passwordError.value = null
        }

        if (phoneNumber.length < 10) {
            _phoneError.value = "Phone number is not valid"
            isValid = false
        } else {
            _phoneError.value = null
        }

        if (fullName.length < 3) {
            _nameError.value = "Please enter your full name"
            isValid = false
        } else {
            _nameError.value = null
        }

        if (isValid) {
            checkIfEmailExists(email) { emailExists ->
                if (emailExists) {
                    _emailError.value = "Email is already in use"
                } else {
                    //Asynchronous call
                    viewModelScope.launch {
                        registerUser(email, password, fullName, phoneNumber)
                    }
                }
            }
        }
    }

    private fun checkIfEmailExists(email: String, callback: (Boolean) -> Unit) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods
                callback(!signInMethods.isNullOrEmpty())
            } else {
                callback(false)
                _registrationError.value = "Error: ${task.exception?.message}"
            }
        }
    }

    // Register the user with Firebase Authentication
    private suspend fun registerUser(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let {
                val userData = User(
                    uid = it.uid,
                    name = fullName,
                    email = email,
                    phoneNumber = phoneNumber,
                    profilePictureUrl = "",
                    isStationOwner = false
                )
                userRepository.createUser(user = userData)
            }
            _registrationSuccess.value = "Welcome, ${auth.currentUser?.email}"

        } catch (e: Exception) {
            Log.e("TAG", "UserViewModel-Error during registration: ${e.message}")
            _registrationError.value = "Authentication failed: ${e.message}"
        }
    }

//Login--------------------------------------------------------------------------------------------------
    fun validateAndLoginUser(email: String, password: String) {
        var isValid = true

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Invalid email format"
            isValid = false
        } else {
            _emailError.value = null
        }

        if (password.isEmpty()) {
            _passwordError.value = "Password cannot be empty"
            isValid = false
        } else {
            _passwordError.value = null
        }

        if (isValid) {
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginSuccess.value = "Welcome, ${auth.currentUser?.email}"
                } else {
                    _loginError.value = "Authentication failed: ${task.exception?.message}"
                }
            }
    }

//--------------------------------------------------------------------------------------------------

    private fun getUserProfile(uid: String) {
        viewModelScope.launch {
            userRepository.getUserByUid(uid)
        }
    }

}