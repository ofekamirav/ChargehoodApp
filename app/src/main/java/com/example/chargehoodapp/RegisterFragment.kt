    package com.example.chargehoodapp

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
    import com.example.chargehoodapp.databinding.RegisterFragmentBinding

    class RegisterFragment:Fragment() {

        private var binding: RegisterFragmentBinding?=null
        private var viewModel: UserViewModel?=null

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding=RegisterFragmentBinding.inflate(inflater,container,false)

            //Initialize the view model
            viewModel= ViewModelProvider(this)[UserViewModel::class.java]


            binding?.RegisterButton?.setOnClickListener {
                val email = binding?.emailEditText?.text.toString().trim()
                val password = binding?.passwordEditText?.text.toString().trim()
                val phoneNumber = binding?.PhoneEditText?.text.toString().trim()
                val fullName = binding?.NameEditText?.text.toString().trim()

                viewModel?.validateAndRegisterUser(email, password, phoneNumber, fullName)
            }
            observeViewModel()
            return binding?.root

        }

        //Observing the view model that listen to live data in the view model
        private fun observeViewModel() {
            viewModel?.emailError?.observe(viewLifecycleOwner, Observer { error ->
                binding?.emailInputLayout?.error = error
            })

            viewModel?.passwordError?.observe(viewLifecycleOwner, Observer { error ->
                binding?.passwordInputLayout?.error = error
            })

            viewModel?.phoneError?.observe(viewLifecycleOwner, Observer { error ->
                binding?.PhoneInputLayout?.error = error
            })

            viewModel?.nameError?.observe(viewLifecycleOwner, Observer { error ->
                binding?.NameInputLayout?.error = error
            })

            viewModel?.registrationSuccess?.observe(viewLifecycleOwner, Observer { message ->
                if (!message.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            })

            viewModel?.registrationError?.observe(viewLifecycleOwner, Observer { error ->
                if (!error.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            })

            viewModel?.registrationSuccess?.observe(viewLifecycleOwner, Observer { message ->
                binding?.progressBar?.visibility = View.VISIBLE
                if (!message.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    //If the registration is successful, navigate to the homepage
                    val action = RegisterFragmentDirections.actionRegisterFragmentToHomepageFragment()
                    binding?.root?.let { view ->
                        Navigation.findNavController(view).navigate(action)
                    }
                }
            })
        }


        override fun onDestroy() {
            super.onDestroy()
            binding=null
        }

        //hide the action bar
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            (activity as? AppCompatActivity)?.supportActionBar?.hide()
        }


    }