package com.example.chargehoodapp.presentation.profile

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.chargehoodapp.databinding.FragmentEditProfileBinding


class EditProfileFragment : Fragment() {

    private var binding: FragmentEditProfileBinding? = null
    private var viewModel: ProfileViewModel? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var galleryLauncher: ActivityResultLauncher<String>? = null
    private var didSetProfileImage: Boolean = false
    private var selectedImageBitmap: Bitmap? = null
    private var selectedImageUri: Uri? = null



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

        // Initialize the camera launcher
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                selectedImageBitmap = bitmap
                binding?.userProfilePic?.setImageBitmap(bitmap)
                didSetProfileImage = true
            }
        }

        // Initialize the gallery launcher
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding?.userProfilePic?.setImageURI(uri)
                didSetProfileImage = true
            }
        }

        binding?.cameraButton?.setOnClickListener{
            cameraLauncher?.launch(null)
        }

        binding?.galleryButton?.setOnClickListener{
            galleryLauncher?.launch("image/*")
        }

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