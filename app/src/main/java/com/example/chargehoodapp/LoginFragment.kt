package com.example.chargehoodapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.chargehoodapp.databinding.LoginFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginFragment: Fragment() {

    private var binding: LoginFragmentBinding?=null
    private var auth: FirebaseAuth?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=LoginFragmentBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()

        binding?.LoginButton?.setOnClickListener(::handleLoginClick)

        binding?.RegisterButton?.setOnClickListener{
            val action=LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        return binding?.root
    }

    private fun handleLoginClick(view: View) {
        val email = binding?.emailEditText?.text.toString().trim()
        val password = binding?.passwordEditText?.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            binding?.emailInputLayout?.error = "Email is required"
            binding?.passwordInputLayout?.error = "Password is required"
        }
        if (password.length < 6) {
            binding?.passwordInputLayout?.error = "Password must be at least 6 characters"
        }
        if (!isEmailValid(email)) {
            binding?.emailInputLayout?.error = "Invalid email format"
        }
        else{
            binding?.emailInputLayout?.error = null
            binding?.passwordInputLayout?.error = null
        }

        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Sign in successful, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success")
                    val user = auth?.currentUser
                    UpdateUI(user)
                } else {
                    //If sign in failes, display a message to the user
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT)
                        .show()
                    UpdateUI(null)
                }
            }



    }

    private fun UpdateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(
                requireContext(),
                "Welcome, ${user.email}",
                Toast.LENGTH_SHORT
            ).show()
//                val mainActivityNavController = requireActivity().findNavController(R.id.main_nav_host)
//                mainActivityNavController.navigate(R.id.HomepageFragment)
        } else {
            // Clear the input fields
            binding?.emailEditText?.text?.clear()
            binding?.passwordEditText?.text?.clear()

        }
    }


    //Helping func that checks if the email is valid
    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(Regex(emailPattern))
    }


    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

}