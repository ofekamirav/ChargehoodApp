package com.example.chargehoodapp.presentation.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import com.example.chargehoodapp.databinding.FragmentEditProfileBinding


class EditProfileFragment : Fragment() {

    private var binding: FragmentEditProfileBinding?= null
    private var viewModel: ProfileViewModel?=null
    private var cameralauncher: ActivityResultLauncher<Void>?=null
    private var gallerylauncher: ActivityResultLauncher<Void>?=null
    private var didSetprofileImage: Boolean = false



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]

        binding?.backButton?.setOnClickListener{
            //Back to profile fragment
            requireActivity().supportFragmentManager.popBackStack()
        }

       // binding?.SaveButton?.setOnClickListener(::UpdateUserDetails)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

//        cameralauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
//                if (bitmap != null) {
//                    binding?.userProfilePic?.setImageBitmap(bitmap)
//                }
//                didSetprofileImage = true
//            }
//
//        binding?.cameraButton?.setOnClickListener{
//            cameralauncher?.launch(null)
//        }
//        binding?.galleryButton?.setOnClickListener{
//            gallerylauncher?.launch("image/*")
//        }
//
//
//        gallerylauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//            if (uri != null) {
//                binding?.userProfilePic?.setImageURI(uri)
//            }
//            didSetprofileImage = true
//        }
        return binding?.root
    }


    fun UpdateUserDetails(){
        val name = binding?.nameEditText?.text.toString()
        val email = binding?.emailEditText?.text.toString().trim()
        val phone = binding?.phoneEditText?.text.toString().trim()
        val password = binding?.passwordEditText?.text.toString().trim()

        viewModel?.updateUser(name, email, phone, password)

    }

}