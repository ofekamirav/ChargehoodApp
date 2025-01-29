package com.example.chargehoodapp.presentation.your_station

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chargehoodapp.data.local.AppLocalDB
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import com.example.chargehoodapp.databinding.AddStationFragmentBinding

class AddStationFragment : Fragment() {

    private var _binding: AddStationFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddStationViewModel

    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var galleryLauncher: ActivityResultLauncher<String>? = null
    private var chosenPic: Bitmap? = null
    private var didSetPic: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddStationFragmentBinding.inflate(inflater, container, false)

        // Initialize database and DAO
        val database = AppLocalDB.database
        val chargingStationDao = database.ChargingStationDao()

        // Initialize repository
        val repository = ChargingStationRepository(chargingStationDao)

        // Inline ViewModel factory (No need for a separate file)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AddStationViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AddStationViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        // Initialize ViewModel
        viewModel = ViewModelProvider(this, factory)[AddStationViewModel::class.java]

        setupObservers()
        setupCameraAndGallery()
        setupRadioButtons()
        setupAddStationButton()

        return binding.root
    }

    private fun setupObservers() {
        viewModel.stationImage.observe(viewLifecycleOwner) { image ->
            image?.let { binding.StationPic.setImageBitmap(it) }
        }

        viewModel.stationAdded.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Charging station added successfully!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                Toast.makeText(requireContext(), "Failed to add station", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCameraAndGallery() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { imageBitmap ->
            if (imageBitmap != null) {
                chosenPic = imageBitmap
                binding.StationPic.setImageBitmap(chosenPic)
                didSetPic = true
                viewModel.setStationImage(imageBitmap)
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                chosenPic = ImageDecoder.decodeBitmap(source)
                binding.StationPic.setImageBitmap(chosenPic)
                didSetPic = true
                viewModel.setStationImage(chosenPic!!)
            }
        }

        binding.cameraButton.setOnClickListener { cameraLauncher?.launch(null) }
        binding.galleryButton.setOnClickListener { galleryLauncher?.launch("image/*") }
    }

    private fun setupRadioButtons() {
        binding.ConnectionTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            val type = when (checkedId) {
                binding.Type1.id -> "Type 1 (SAE J1772)"
                binding.Type2.id -> "Type 2 (IEC 62196-2)"
                binding.CCS.id -> "CCS"
                binding.CHAdeMO.id -> "CHAdeMO"
                else -> null
            }
            type?.let { viewModel.setConnectionType(it) }
        }

        binding.chargingSpeedGroup.setOnCheckedChangeListener { _, checkedId ->
            val speed = when (checkedId) {
                binding.speedSlow.id -> "Level 1 (3-5 kW)"
                binding.speedMedium.id -> "Level 2 (7-22 kW)"
                binding.speedMax.id -> "Level 3 (50+ kW)"
                else -> null
            }
            speed?.let { viewModel.setChargingSpeed(it) }
        }
    }

    private fun setupAddStationButton() {
        binding.AddStationButton.setOnClickListener {
            val price = binding.PriceEditText.text.toString()
            viewModel.setPrice(price)

            val connectionType = viewModel.connectionType.value
            val chargingSpeed = viewModel.chargingSpeed.value
            val image = viewModel.stationImage.value

            if (connectionType == null || chargingSpeed == null || price.isBlank() || image == null) {
                Toast.makeText(requireContext(), "Please complete all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.createChargingStation(
                ownerId = "user1", // Replace with actual user ID from Firebase auth
                latitude = 32.0853, // Replace with real GPS data
                longitude = 34.7818,
                addressName = "Tel Aviv - Example Location"
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
