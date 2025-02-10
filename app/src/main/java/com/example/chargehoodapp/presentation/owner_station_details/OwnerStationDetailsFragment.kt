package com.example.chargehoodapp.presentation.owner_station_details

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chargehoodapp.R
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.databinding.FragmentOwnerStationDetailsBinding
import com.example.chargehoodapp.NavGraphDirections



class OwnerStationDetailsFragment : DialogFragment() {


    private var binding: FragmentOwnerStationDetailsBinding? = null
    private var viewModel: OwnerStationDetailsViewModel? = null
    private var currentStation: ChargingStation? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val window = dialog.window
            window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.85).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return dialog
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOwnerStationDetailsBinding.inflate(inflater,container,false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[OwnerStationDetailsViewModel::class.java]

        //Set the station from the shared preferences
        MyApplication.Globals.selectedStation?.let { station ->
            Log.d("TAG", "OwnerStationDetailsFragment-Station: $station")
            viewModel?.setStation(station)
            bindStationDetails(station)
        }

        binding?.EditButton?.setOnClickListener {
            dismiss()
            val action = NavGraphDirections.actionGlobalEditStationFragment(currentStation?.id ?: "")
            findNavController().navigate(action)
            Log.d("TAG", "OwnerStationDetailsFragment - Edit button clicked")
        }



        binding?.cancelButton?.setOnClickListener {
            dismiss()
        }
    }

    // Bind the station details to the UI
    private fun bindStationDetails(station: ChargingStation) {
        currentStation = station
        binding?.apply {
            Glide.with(requireContext()).load(station.imageUrl).into(imageView)
            addressTextView.text = station.addressName
            availabilityTextView.text = if (station.availability) "Available" else "Unavailable"
            val colorRes = if (station.availability) R.color.green else R.color.red
            availabilityTextView.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
            chargingSpeedTextView.text = station.chargingSpeed
            priceTextView.text = "${station.pricePerkW} $"
            connectionTypeTextView.text = station.connectionType
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}