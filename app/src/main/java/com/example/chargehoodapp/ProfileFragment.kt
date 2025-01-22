package com.example.chargehoodapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chargehoodapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding?=null
    private var viewModel: UserViewModel?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel
        viewModel= ViewModelProvider(this)[UserViewModel::class.java]

        binding?.contentGroup?.visibility = View.GONE
        binding?.progressBar?.visibility = View.VISIBLE

        // Get the current user
        viewModel?.getCurrentUser()

        // Observe the currentUser LiveData
        ObserveCurrentUserDetails()

        // Back button functionality
        binding?.backButton?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding?.LogoutButton?.setOnClickListener{
            viewModel?.logout()
            val action=ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
            findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentProfileBinding.inflate(inflater,container,false)

        return binding?.root
    }

    //Set the current user details in the UI
    private fun ObserveCurrentUserDetails() {
        viewModel?.currentUser?.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d("TAG", "ProfileFragment-Updating UI with user details: ${user.name}, ${user.email}")

                // Update the UI with the user details
                binding?.apply {
                    nameTextView.text = user.name
                    emailTextView.text = user.email
                    phoneTextView.text = user.phoneNumber
                    passwordTextView.text = "********"

                    if (user.profilePictureUrl.isNotEmpty()) {
                        Glide.with(this@ProfileFragment)
                            .load(user.profilePictureUrl)
                            .placeholder(R.drawable.default_profile_pic)
                            .into(userProfilePic)
                    } else {
                        userProfilePic.setImageResource(R.drawable.default_profile_pic)
                    }
                    contentGroup.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            } else {
                Log.d("TAG", "ProfileFragment-Current user is null")
            }
        }
    }


}