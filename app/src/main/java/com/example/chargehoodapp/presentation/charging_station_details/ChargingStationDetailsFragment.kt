package com.example.chargehoodapp.presentation.charging_station_details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.databinding.StationDetailsCardBinding

class ChargingStationDetailsFragment: Fragment() {


    private var binding: StationDetailsCardBinding? = null
    private var viewModel: ChargingStationDetailsViewModel? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[ChargingStationDetailsViewModel::class.java]

//        Log.d("TAG", "DetailsFragment-Received stationId: $stationId")
//        if (!stationId.isNullOrEmpty()) {
//            viewModel?.loadChargingStationDetails(stationId)
//        }

        //Update UI with station details
        viewModel?.chargingStation?.observe(viewLifecycleOwner) { station ->
            Log.d("TAG", "DetailsFragment-Displaying station details: ${station?.id}")
            bindStationDetails(viewModel?.ownerName?.value, viewModel?.ownerPhoneNumber?.value)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StationDetailsCardBinding.inflate(inflater, container, false)

        binding?.SMSLinkTextView?.setOnClickListener{
            val phoneNumber = binding?.SMSLinkTextView?.text.toString()
            val message = "Hello, I'm interested in charging your station."
            openSmsApp(phoneNumber, message)
        }
        binding?.cancelButton?.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding?.NavigationButton?.setOnClickListener{
            openWaze()
        }

        return binding?.root
    }

    private fun openSmsApp(phoneNumber: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:$phoneNumber") // Protocol SMS
                putExtra("sms_body", message)       // Sms body
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            MyApplication.Globals.context?.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "ChargingStationDetailsViewModel-Error opening SMS app: ${e.message}")
            Toast.makeText(requireContext(), "Unable to open messaging app.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openWaze() {
        val locationUrl = viewModel?.chargingStation?.value?.wazeUrl

        if (!locationUrl.isNullOrEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(locationUrl)
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unable to open Waze. Please check if the app is installed.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No Waze link available for this location.", Toast.LENGTH_SHORT).show()
        }
    }




    private fun bindStationDetails(ownerName: String?, ownerPhoneNumber: String?) {
        binding?.apply {
            nameTextView.text = ownerName
            SMSLinkTextView.text = ownerPhoneNumber
            imageView.setImageURI(viewModel?.chargingStation?.value?.imageUrl?.toUri())
            //addressTextView.text = viewModel?.chargingStation?.value?.location.toString()
            availabilityTextView.text = if (viewModel?.chargingStation?.value?.availability == true) "Available" else "Unavailable"
            chargingSpeedTextView.text = viewModel?.chargingStation?.value?.chargingSpeed
            priceTextView.text = viewModel?.chargingStation?.value?.pricePerkW.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}