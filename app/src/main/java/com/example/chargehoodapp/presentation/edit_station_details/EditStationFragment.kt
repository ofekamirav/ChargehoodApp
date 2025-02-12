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
import com.bumptech.glide.Glide
import com.example.chargehoodapp.R
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.model.User
import com.example.chargehoodapp.databinding.EditStationFragmentBinding
import com.example.chargehoodapp.presentation.edit_station_details.EditStationViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditStationFragment : Fragment() {

    private var binding: EditStationFragmentBinding? = null
    private var viewModel: EditStationViewModel? = null

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
        binding = EditStationFragmentBinding.inflate(inflater, container, false)

        setupCameraAndGallery()

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[EditStationViewModel::class.java]

        setupObservers()

        val stationId = arguments?.getString("stationId")
        stationId?.let {
            viewModel?.getChargingStationById(it)?.observe(viewLifecycleOwner) { station ->
                if (station != null) {
                    viewModel?.setOriginalStation(station)
                    populateFields(station)
                } else {
                    Log.e("TAG", "Station not found with ID: $it")
                }
            }
        }


        binding?.UpdateStationButton?.setOnClickListener {
            binding?.progressBar?.visibility = View.VISIBLE

            val connectionType = when (binding?.UpdateConnectionTypeGroup?.checkedRadioButtonId) {
                binding?.Type1?.id -> "Type 1 (SAE J1772)"
                binding?.Type2?.id -> "Type 2 (IEC 62196-2)"
                else -> null
            }

            val chargingSpeed = when (binding?.UpdatechargingSpeedGroup?.checkedRadioButtonId) {
                binding?.speedSlow?.id -> "3.7 kW"
                binding?.speedMedium?.id -> "11 kW"
                binding?.MaxSpeed?.id -> "22 kW"
                else -> null
            }

            val price = binding?.UpdatePriceEditText?.text.toString().toDoubleOrNull()

            viewModel?.updateChargingStation(
                lat = selectedLat,
                long = selectedLng,
                address = selectedAddress,
                connectionType = connectionType,
                chargingSpeed = chargingSpeed,
                price = price,
                imageUrl = chosenPic
            )
        }

        binding?.UpdatecameraButton?.setOnClickListener {
            cameraLauncher?.launch(null)
        }
        binding?.UpdategalleryButton?.setOnClickListener {
            galleryLauncher?.launch("image/*")
        }

        binding?.UpdateBackButton?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObservers() {
        viewModel?.updateStatus?.observe(viewLifecycleOwner) { success ->
            binding?.progressBar?.visibility = View.GONE
            if (success == true) {
                Toast.makeText(requireContext(), "Station updated successfully!", Toast.LENGTH_SHORT).show()
                viewModel?.clearLiveData()
                findNavController().navigateUp()
            }
            if(success==false){
                Toast.makeText(requireContext(), "Failed to update station", Toast.LENGTH_SHORT).show()
                viewModel?.clearLiveData()
            }
        }

        viewModel?.errorMessage?.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCameraAndGallery() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { imageBitmap ->
            if (imageBitmap != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        chosenPic = imageBitmap
                        binding?.UpdateStationPic?.setImageBitmap(chosenPic)
                        didSetPic = true
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
                        binding?.UpdateStationPic?.setImageBitmap(bitmap)
                        didSetPic = true
                    }
                }
            }
        }
    }

    //Set the current station details in fields--------------------------------------------------------------------------------
    private fun populateFields(station: ChargingStation) {
        Log.d("TAG", "EditStationFragment-populateFields called with station: $station")

        binding?.apply {
            if (station.imageUrl.isNotEmpty()) {
                Glide.with(this@EditStationFragment)
                    .load(station.imageUrl)
                    .placeholder(R.drawable.default_station_pic)
                    .into(UpdateStationPic)
            }
            addressTextView.text = station.addressName

            when (station.connectionType) {
                "Type 1 (SAE J1772)" -> UpdateConnectionTypeGroup.check(Type1.id)
                "Type 2 (IEC 62196-2)" -> UpdateConnectionTypeGroup.check(Type2.id)
            }

            when (station.chargingSpeed) {
                "3.7 kW" -> UpdatechargingSpeedGroup.check(speedSlow.id)
                "11 kW" -> UpdatechargingSpeedGroup.check(speedMedium.id)
                "22 kW" -> UpdatechargingSpeedGroup.check(MaxSpeed.id)
            }

            UpdatePriceEditText.setText(station.pricePerkW.toString())
        }
    }

//------------------------------------------------------------------------------------------------------------------------------

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
