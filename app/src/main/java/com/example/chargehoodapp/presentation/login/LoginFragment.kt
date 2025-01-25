package com.example.chargehoodapp.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.chargehoodapp.databinding.LoginFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment: Fragment() {

    private var binding: LoginFragmentBinding?=null
    private var viewModel: LoginViewModel?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=LoginFragmentBinding.inflate(inflater,container,false)

        //Initialize the view model
        viewModel= ViewModelProvider(this)[LoginViewModel::class.java]

        binding?.LoginButton?.setOnClickListener{
            val email = binding?.emailEditText?.text.toString().trim()
            val password = binding?.passwordEditText?.text.toString().trim()

            viewModel?.validateAndLoginUser(email, password)
        }
        observeViewModel()

        binding?.RegisterButton?.setOnClickListener{
            val action= LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
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