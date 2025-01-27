package com.example.chargehoodapp.presentation.charging_station_details

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.databinding.StationDetailsCardBinding

class ChargingStationDetailsFragment: DialogFragment() {


    private var binding: StationDetailsCardBinding? = null
    private var viewModel: ChargingStationDetailsViewModel? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val window = dialog.window
            window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.85).toInt(), // 85% מרוחב המסך
                ViewGroup.LayoutParams.WRAP_CONTENT // גובה מותאם לפי התוכן
            )
        }
        return dialog
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


        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[ChargingStationDetailsViewModel::class.java]

        val selectedStation = MyApplication.Globals.selectedStation
        selectedStation?.let {
            viewModel?.loadChargingStationDetails(it.id.toString())
        }

        //Update UI with station details
        viewModel?.chargingStation?.observe(viewLifecycleOwner) { station ->
            station?.let {
                bindStationDetails(it)
            }
        }

        viewModel?.ownerName?.observe(viewLifecycleOwner) { name ->
            binding?.nameTextView?.text = name ?: "Unknown Owner"
        }

        viewModel?.ownerPhoneNumber?.observe(viewLifecycleOwner) { phoneNumber ->
            binding?.SMSLinkTextView?.text = phoneNumber ?: "Unknown Phone Number"
        }

        binding?.cancelButton?.setOnClickListener {
            dismiss()
        }


        binding?.NavigationButton?.setOnClickListener{
            openWaze()
        }

//        binding?.startChargingButton?.setOnClickListener {
//            selectedStation?.let { station ->
//                if (checkAvailabilityAndPayment(station)) {
//                    val action = ChargingStationDetailsFragmentDirections.actionChargingStationDetailsFragmentToChargingPageFragment()
//                    findNavController().navigate(action)
//                }
//                else{
//                    Log.d("TAG", "ChargingStationDetailsFragment-Error starting charging session")
//                }
//
//                }
//        }

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

    // Bind the station details to the UI
    private fun bindStationDetails(station: ChargingStation) {
        binding?.apply {
            Glide.with(requireContext())
                .load(station.imageUrl)
                .into(imageView)
            addressTextView.text = station.addressName
            availabilityTextView.text = if (station.availability) "Available" else "Unavailable"
            chargingSpeedTextView.text = station.chargingSpeed
            priceTextView.text = station.pricePerkW.toString()
        }
    }

    //Check if the station is available and if there is payment information
    private fun checkAvailabilityAndPayment(station: ChargingStation): Boolean {
        viewModel?.chargingStation?.value?.let { station ->
            if (station.availability) {
                viewModel?.currentUserPaymentBoolean?.value?.let { paymentBoolean ->
                    if (paymentBoolean) {
                        return true
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please add a payment method.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "This station is not available.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}