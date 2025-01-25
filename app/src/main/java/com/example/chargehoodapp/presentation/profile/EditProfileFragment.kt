package com.example.chargehoodapp.presentation.profile

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chargehoodapp.R
import com.example.chargehoodapp.data.model.User
import com.example.chargehoodapp.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private var binding: FragmentEditProfileBinding? = null
    private var viewModel: ProfileViewModel? = null
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var galleryLauncher: ActivityResultLauncher<String>? = null
    private var didSetProfileImage: Boolean = false
    private var selectedImageBitmap: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG", "EditProfileFragment-onViewCreated called")

        viewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]

        // Show progress bar initially
        binding?.contentGroup?.visibility = View.GONE
        binding?.progressBar?.visibility = View.VISIBLE

        // Get current user data
        viewModel?.getCurrentUser()

        // Observe current user data to pre-fill fields
        viewModel?.currentUser?.observe(viewLifecycleOwner) { user ->
            Log.d("TAG", "EditProfileFragment-currentUser observed: $user")
            if (user != null) {
                populateFields(user)
                binding?.progressBar?.visibility = View.GONE
                binding?.contentGroup?.visibility = View.VISIBLE
            } else {
                showErrorDialog("Failed to load user details. Please try again.")
            }
        }

        binding?.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        // Camera and gallery actions
        binding?.cameraButton?.setOnClickListener {
            cameraLauncher?.launch(null)
        }

        binding?.galleryButton?.setOnClickListener {
            galleryLauncher?.launch("image/*")
        }

        binding?.SaveButton?.setOnClickListener{
            binding?.progressBar?.visibility = View.VISIBLE
            updateUserDetails()
        }
    }

    private fun populateFields(user: User) {
        Log.d("TAG", "EditProfileFragment-populateFields called with user: $user")
        binding?.apply {
            nameEditText.setText(user.name)
            emailEditText.setText(user.email)
            phoneEditText.setText(user.phoneNumber)
            passwordEditText.setText("")

            if (user.profilePictureUrl.isNotEmpty()) {
                Glide.with(this@EditProfileFragment)
                    .load(user.profilePictureUrl)
                    .circleCrop()
                    .placeholder(R.drawable.default_profile_pic)
                    .into(userProfilePic)
            }
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> findNavController().navigateUp() }
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "EditProfileFragment-onCreateView called")
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        // Initialize camera and gallery launchers
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                selectedImageBitmap = bitmap
                binding?.userProfilePic?.setImageBitmap(bitmap)
                didSetProfileImage = true
            }
        }

        // Initialize gallery launcher
        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                binding?.userProfilePic?.setImageBitmap(selectedImageBitmap)
                didSetProfileImage = true
            }
        }

        return binding?.root
    }

    private fun updateUserDetails() {
        Log.d("TAG", "EditProfileFragment-updateUserDetails called")
        val name = binding?.nameEditText?.text?.toString()?.takeIf { it.isNotEmpty() }
        val email = binding?.emailEditText?.text?.toString()?.takeIf { it.isNotEmpty() }
        val phone = binding?.phoneEditText?.text?.toString()?.takeIf { it.isNotEmpty() }
        val password = binding?.passwordEditText?.text?.toString()?.takeIf { it.isNotEmpty() }

        Log.d("TAG", "EditProfileFragment-Inputs: name=$name, email=$email, phone=$phone, password=$password")

        val imageBitmap = if (didSetProfileImage) {
            (binding?.userProfilePic?.drawable as? BitmapDrawable)?.bitmap
        } else null
        Log.d("TAG", "EditProfileFragment-ImageBitmap set: ${imageBitmap != null}")

        // Validate inputs
        val isValid = validateInputIfExist(name, email, phone, password)
        Log.d("TAG", "EditProfileFragment-Input validation result: $isValid")

        if (!isValid) {
            Log.e("TAG", "EditProfileFragment-Input validation failed.")
            binding?.progressBar?.visibility = View.GONE
            showErrorDialog("Please check your inputs")
            return
        }
        Log.d("TAG", "EditProfileFragment-Starting updateUserProfile in ViewModel")

        viewModel?.updateUserProfile(
            name = name,
            email = email,
            phone = phone,
            password = password,
            image = imageBitmap
        )
        Log.d("TAG", "EditProfileFragment-Observing updateStatus LiveData")
        viewModel?.updateStatus?.observe(viewLifecycleOwner) { status ->
            Log.d("TAG", "EditProfileFragment-updateStatus observed: $status")
            if (!status.isNullOrEmpty()) {
                binding?.progressBar?.visibility = View.GONE
                AlertDialog.Builder(requireContext())
                    .setTitle(status)
                    .setPositiveButton("OK") { _, _ ->
                        val action =
                            EditProfileFragmentDirections.actionEditProfileFragmentToHomepageFragment()
                        findNavController().navigate(action)
                    }
                    .show()
            } else{
                showErrorDialog("Failed to update profile. Please try again.")
            }
        }

    }

    private fun validateInputIfExist(name: String?, email: String?, phone: String?, password: String?):Boolean {
        var isValid = true

        if (name == null && email == null && phone == null && password == null) {
            return isValid
        }
        if(email !=null && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding?.emailInputLayout?.error = "Invalid email format"
            isValid = false
        }
        if (phone != null && phone.length < 10) {
            binding?.phoneInputLayout?.error = "Phone number is not valid"
            isValid = false
        }
        if (password != null && password.length < 6) {
            binding?.passwordInputLayout?.error = "Password must be at least 6 characters"
            isValid = false
        }
        if (name != null && name.length < 3) {
            binding?.nameInputLayout?.error = "Please enter your full name"
            isValid = false
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
