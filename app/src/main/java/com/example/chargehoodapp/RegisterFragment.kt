package com.example.chargehoodapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chargehoodapp.databinding.RegisterFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterFragment:Fragment() {

    private var binding: RegisterFragmentBinding?=null
    private var auth: FirebaseAuth?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=RegisterFragmentBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()

        binding?.RegisterButton?.setOnClickListener(::handleRegisterClick)



        return binding?.root

    }

    private fun handleRegisterClick(view: View){
        val email=binding?.emailEditText?.text.toString().trim()
        val password=binding?.passwordEditText?.text.toString().trim()
        val phoneNumber=binding?.PhoneEditText?.text.toString().trim()
        val fullName=binding?.NameEditText?.text.toString().trim()

        if(email.isEmpty()||password.isEmpty()||phoneNumber.isEmpty()||fullName.isEmpty()){
            binding?.emailInputLayout?.error="Email is required"
            binding?.passwordInputLayout?.error="Password is required"
            binding?.PhoneInputLayout?.error="Phone number is required"
            binding?.NameInputLayout?.error="Full name is required"
        }
        if(password.length <6){
            binding?.passwordInputLayout?.error="Password must be at least 6 characters"
        }
        if(phoneNumber.length <10){
            binding?.PhoneInputLayout?.error="Phone number is not valid"
        }
        if(fullName.length <3){
            binding?.NameInputLayout?.error="Please enter your full name"
        }
        if (!isEmailValid(email)) {
            binding?.emailInputLayout?.error = "Invalid email format"
            return
        }
        checkIfEmailExists(email) { emailExists ->
            if (emailExists) {
                binding?.emailInputLayout?.error = "Email is already in use"
            } else {
                binding?.emailInputLayout?.error = null
            }
        }
        registerUser(email,password)

    }

    //Helping func that checks if the email is valid
     private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(Regex(emailPattern))
    }

    //Helping func that checks if the email already exists
    private fun checkIfEmailExists(email: String, callback: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    callback(!signInMethods.isNullOrEmpty())
                } else {
                    task.exception?.let { exception ->
                        callback(false)
                        Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

    //Register the user to firebase DB
    private fun registerUser(email: String, password: String){
        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    // Registration successful, Update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth?.currentUser
                    UpdateUI(user)
                }else{
                    //if sign in fails, display a message to the user
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed",
                        Toast.LENGTH_SHORT,
                    ).show()
                    UpdateUI(null)
                }
            }
    }
    private fun UpdateUI(user: FirebaseUser?){
        if (user != null) {
            Toast.makeText(
                requireContext(),
                "Welcome, ${user.email}",
                Toast.LENGTH_SHORT
            ).show()
//            val mainActivityNavController = requireActivity().findNavController(R.id.main_nav_host)
//            mainActivityNavController.navigate(R.id.HomepageFragment)
        } else {
            // Clear the input fields
            binding?.emailEditText?.text?.clear()
            binding?.passwordEditText?.text?.clear()
            binding?.PhoneEditText?.text?.clear()
            binding?.NameEditText?.text?.clear()
        }

    }







}