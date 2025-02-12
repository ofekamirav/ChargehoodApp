package com.example.chargehoodapp.presentation.your_station


import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chargehoodapp.R
import com.example.chargehoodapp.databinding.AddStationFragmentBinding
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddStationFragment : Fragment() {

    private var binding: AddStationFragmentBinding? = null
    private var viewModel: AddStationViewModel? = null

    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var galleryLauncher: ActivityResultLauncher<String>? = null

    private var chosenPic: Bitmap? = null
    private var didSetPic: Boolean = false
    private var selectedAddress: String? = null
    private var selectedLat: Double? = null
    private var selectedLng: Double? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddStationFragmentBinding.inflate(inflater, container, false)

        setupCameraAndGallery()
        setupRadioButtons()

        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity())[AddStationViewModel::class.java]

        setupObservers()
        setupAddStationButton()
        setupAutoComplete()

        binding?.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }

    }



    private fun setupObservers() {
        viewModel?.stationAdded?.observe(viewLifecycleOwner) { success ->
            if (success==true) {
                binding?.progressBar?.visibility = View.GONE
                Toast.makeText(requireContext(), "Station added successfully!", Toast.LENGTH_SHORT).show()
                viewModel?.clearLiveData()
                findNavController().navigateUp()
            }
            if(success==false){
                Toast.makeText(requireContext(), "Failed to add station", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel?.errorMessage?.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

// Setup camera and gallery----------------------------------------------------------------------------------------------------------------------------------------
    private fun setupCameraAndGallery() {

    cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { imageBitmap ->
        if (imageBitmap != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    chosenPic = imageBitmap
                    binding?.StationPic?.setImageBitmap(chosenPic)
                    didSetPic = true
                    viewModel?.setStationImage(imageBitmap)
                }
            }
        }
    }

    galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)

                withContext(Dispatchers.Main) {
                    chosenPic = bitmap
                    binding?.StationPic?.setImageBitmap(bitmap)
                    didSetPic = true
                    viewModel?.setStationImage(bitmap)
                }
            }
        }
    }



    binding?.cameraButton?.setOnClickListener { cameraLauncher?.launch(null) }
        binding?.galleryButton?.setOnClickListener { galleryLauncher?.launch("image/*") }
    }


//Setup the radio button selection--------------------------------------------------------------------------------------------------------------------------------------
    private fun setupRadioButtons() {
        binding?.ConnectionTypeGroup?.setOnCheckedChangeListener { _, checkedId ->
            val type = when (checkedId) {
                binding?.Type1?.id -> "Type 1 (SAE J1772)"
                binding?.Type2?.id -> "Type 2 (IEC 62196-2)"
                else -> null
            }
            type?.let { viewModel?.setConnectionType(it) }
        }

        binding?.chargingSpeedGroup?.setOnCheckedChangeListener { _, checkedId ->
            val speed = when (checkedId) {
                binding?.speedSlow?.id -> "3.7 kW"
                binding?.speedMedium?.id -> "11 kW"
                binding?.MaxSpeed?.id -> "22 kW"
                else -> null
            }
            speed?.let { viewModel?.setChargingSpeed(it) }
        }
    }

//Handle add station button click--------------------------------------------------------------------------------------------------------------------------------
    private fun setupAddStationButton() {
    binding?.AddStationButton?.setOnClickListener {
        if (selectedAddress == null || selectedLat == null || selectedLng == null) {
            Toast.makeText(requireContext(), "Please select a location", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }

        val price = binding?.PriceEditText?.text.toString()
        viewModel?.setPrice(price)


        if (didSetPic) {
            binding?.progressBar?.visibility = View.VISIBLE
            viewModel?.createChargingStation(
                latitude = selectedLat ?: 0.0,
                longitude = selectedLng ?: 0.0,
                addressName = selectedAddress ?: ""
            )
        } else
            Toast.makeText(requireContext(), "Please select a picture", Toast.LENGTH_SHORT).show()
    }
}

//Setup the autocomplete input with Google Places---------------------------------------------------------------------------------------------------------

    private fun setupAutoComplete() {
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        customizeAutoCompleteUI(autocompleteFragment)

        autocompleteFragment.setPlaceFields(
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        )

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                selectedAddress = place.address
                selectedLat = place.latLng?.latitude
                selectedLng = place.latLng?.longitude

                Log.d("GooglePlaces", "Address: $selectedAddress, Lat: $selectedLat, Lng: $selectedLng")
            }

            override fun onError(status: Status) {
                Log.e("GooglePlaces", "Error: $status")
            }
        })

        customizeAutoCompleteUI(autocompleteFragment)
    }

    private fun customizeAutoCompleteUI(fragment: AutocompleteSupportFragment) {
        fragment.view?.let { autoCompleteView ->
            autoCompleteView.setBackgroundColor(Color.TRANSPARENT)

            autoCompleteView.viewTreeObserver.addOnGlobalLayoutListener {
                val editText = autoCompleteView.findViewWithTag<EditText>("SearchInput")
                editText?.apply {
                    setBackgroundColor(Color.TRANSPARENT)
                    setHintTextColor(Color.GRAY)
                    setTextColor(Color.BLACK)
                    background = null
                }
            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
