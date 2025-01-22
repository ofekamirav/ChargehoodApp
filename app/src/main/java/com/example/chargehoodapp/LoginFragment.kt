package com.example.chargehoodapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.chargehoodapp.databinding.LoginFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginFragment: Fragment() {

    private var binding: LoginFragmentBinding?=null
    private var auth: FirebaseAuth?=null
    private var viewModel: UserViewModel?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=LoginFragmentBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()

        //Initialize the view model
        viewModel= ViewModelProvider(this)[UserViewModel::class.java]

        binding?.LoginButton?.setOnClickListener{
            val email = binding?.emailEditText?.text.toString().trim()
            val password = binding?.passwordEditText?.text.toString().trim()

            viewModel?.validateAndLoginUser(email, password)
        }
        observeViewModel()

        binding?.RegisterButton?.setOnClickListener{
            val action=LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        return binding?.root
    }

    private fun observeViewModel() {
        // Email error
        viewModel?.emailError?.observe(viewLifecycleOwner, Observer { error ->
            binding?.emailInputLayout?.error = error
        })

        // Password error
        viewModel?.passwordError?.observe(viewLifecycleOwner, Observer { error ->
            binding?.passwordInputLayout?.error = error
        })

        // Login success
        viewModel?.loginSuccess?.observe(viewLifecycleOwner, Observer { message ->
            binding?.progressBar?.visibility = View.VISIBLE
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                // Navigate to homepage after successful login
                val action = LoginFragmentDirections.actionLoginFragmentToHomepageFragment()
                binding?.root?.let { view ->
                    Navigation.findNavController(view).navigate(action)
                }
            }
        })

        // Login error
        viewModel?.loginError?.observe(viewLifecycleOwner, Observer { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //hide the action bar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

}