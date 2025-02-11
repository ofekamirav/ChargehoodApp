package com.example.chargehoodapp.presentation.profile

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chargehoodapp.R
import com.example.chargehoodapp.data.model.User
import com.example.chargehoodapp.databinding.FragmentEditProfileBinding
import kotlinx.coroutines.launch

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

        observeUpdateStatus()
        observeCurrentUser()

        // Show progress bar initially
        binding?.contentGroup?.visibility = View.GONE
        binding?.progressBar?.visibility = View.VISIBLE

        // Get current user data
        viewModel?.getCurrentUser()


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

        binding?.SaveButton?.setOnClickListener {
            val newPassword = binding?.passwordEditText?.text?.toString()?.takeIf { it.isNotEmpty() }
            val newEmail = binding?.emailEditText?.text?.toString()?.takeIf { it.isNotEmpty() }

            var credentialsUpdatedSuccessfully = false

            if (newPassword != null || newEmail != null) {
                promptForCurrentPassword { currentPassword ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        try {
                            viewModel?.reauthenticateAndUpdateCredentials(currentPassword, newPassword, newEmail)
                            credentialsUpdatedSuccessfully = true
                            updateUserDetails(credentialsUpdatedSuccessfully)
                        } catch (e: Exception) {
                            binding?.progressBar?.visibility = View.GONE
                            showErrorDialog("Re-authentication failed: ${e.message}")
                        }
                    }
                }
            } else {
                updateUserDetails(credentialsUpdatedSuccessfully)
            }
        }

    }

    private fun observeCurrentUser() {
        viewModel?.currentUser?.observe(viewLifecycleOwner) { user ->
            binding?.progressBar?.visibility = View.GONE
            binding?.contentGroup?.visibility = View.VISIBLE
            user?.let {
                populateFields(it)
            } ?: run {
                showErrorDialog("Failed to load user data.")
            }
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

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Success")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                navigateToHomepage()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToHomepage() {
        binding?.progressBar?.visibility = View.GONE
        findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToHomepageFragment())
        findNavController().popBackStack(R.id.editProfileFragment, true)
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun observeUpdateStatus() {
        viewModel?.updateStatus?.observe(viewLifecycleOwner) { status ->
            Log.d("TAG", "EditProfileFragment-updateStatus observed: $status")
            binding?.progressBar?.visibility = View.VISIBLE

            status?.let {
                when {
                    it.contains("updated successfully", ignoreCase = true) -> {
                        binding?.progressBar?.visibility = View.GONE
                        showSuccessDialog(it)
                        viewModel?.resetUpdateStatus()
                    }
                    it.contains("Error", ignoreCase = true) || it.contains("Failed", ignoreCase = true) -> {
                        binding?.progressBar?.visibility = View.GONE
                        showErrorDialog(it)
                        viewModel?.resetUpdateStatus()
                    }
                }
            }
        }
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

    private fun updateUserDetails(credentialsUpdatedSuccessfully: Boolean) {
        binding?.progressBar?.visibility = View.VISIBLE
        val name = binding?.nameEditText?.text?.toString()?.takeIf { it.isNotEmpty() }
        val email = binding?.emailEditText?.text?.toString()?.takeIf { it.isNotEmpty() }
        val phone = binding?.phoneEditText?.text?.toString()?.takeIf { it.isNotEmpty() }
        val password = binding?.passwordEditText?.text?.toString()?.takeIf { it.isNotEmpty() }

        if (!validateInputIfExist(name, email, phone, password)) {
            binding?.progressBar?.visibility = View.GONE
            return
        }

        val currentUser = viewModel?.currentUser?.value
        val updatesNeeded = (name != currentUser?.name || phone != currentUser?.phoneNumber || didSetProfileImage)

        if (!updatesNeeded && credentialsUpdatedSuccessfully) {
            binding?.progressBar?.visibility = View.GONE
            return
        } else if (!updatesNeeded) {
            showErrorDialog("No changes detected.")
            binding?.progressBar?.visibility = View.GONE
            return
        }

        viewModel?.updateUserProfile(name, email, phone, selectedImageBitmap)
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
        if (phone != null && phone.length != 10) {
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

    private fun promptForCurrentPassword(onPasswordEntered: (String) -> Unit) {
        val input = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Enter current password"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Re-authentication Required")
            .setMessage("Please enter your current password to continue.")
            .setView(input)
            .setPositiveButton("Confirm") { _, _ ->
                val currentPassword = input.text.toString()
                if (currentPassword.isNotEmpty()) {
                    Log.d("TAG", "Password entered for reauthentication: $currentPassword")
                    onPasswordEntered(currentPassword)
                    binding?.progressBar?.visibility = View.VISIBLE
                } else {
                    showErrorDialog("Password cannot be empty.")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel?.updateStatus?.removeObservers(viewLifecycleOwner)
        viewModel?.resetUpdateStatus()
        binding = null
    }
}
