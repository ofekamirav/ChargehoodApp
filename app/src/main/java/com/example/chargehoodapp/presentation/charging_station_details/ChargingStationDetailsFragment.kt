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
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chargehoodapp.R
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.databinding.StationDetailsCardBinding
import com.example.chargehoodapp.presentation.charging_page.ChargingPageViewModel

class ChargingStationDetailsFragment: DialogFragment() {


    private var binding: StationDetailsCardBinding? = null
    private var viewModel: ChargingStationDetailsViewModel? = null
    private val chargingPageViewModel: ChargingPageViewModel by activityViewModels()


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
        binding = StationDetailsCardBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[ChargingStationDetailsViewModel::class.java]

        binding?.stationDetailsCard?.visibility = View.VISIBLE
        binding?.progressBar?.visibility = View.VISIBLE
        binding?.contentGroup?.visibility = View.GONE


        //Set the station from the shared preferences
        MyApplication.Globals.selectedStation?.let { station ->
            Log.d("TAG", "ChargingStationDetailsFragment-Station: $station")
            viewModel?.setStation(station)
            viewModel?.loadOwnerDetails(station)
            viewModel?.setUserPaymentBoolean()
            chargingPageViewModel.setStation(station)
        }

        viewModel?.chargingStation?.observe(viewLifecycleOwner) { station ->
            station?.let { bindStationDetails(it) }
        }

        viewModel?.ownerName?.observe(viewLifecycleOwner) { name ->
            binding?.nameTextView?.text = name ?: "Unknown Owner"
        }

        viewModel?.ownerPhoneNumber?.observe(viewLifecycleOwner) { phoneNumber ->
            binding?.SMSLinkTextView?.text = phoneNumber ?: "Unknown Phone Number"
        }

        viewModel?.isLoading?.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding?.progressBar?.visibility = View.VISIBLE
                binding?.contentGroup?.visibility = View.GONE
            } else {
                binding?.progressBar?.visibility = View.GONE
                binding?.contentGroup?.visibility = View.VISIBLE
            }
        }

        binding?.SMSLinkTextView?.setOnClickListener{
            val phoneNumber = binding?.SMSLinkTextView?.text.toString()
            val message = "Hello, I'm interested in charging your station."
            openSmsApp(phoneNumber, message)
        }

        binding?.cancelButton?.setOnClickListener {
            dismiss()
        }

        binding?.NavigationButton?.setOnClickListener{
            openWaze()
        }

        binding?.startChargingButton?.setOnClickListener {
            checkAndStartCharging()
        }

    }


    private fun checkAndStartCharging() {
        val station = viewModel?.chargingStation?.value ?: return
        val isPaymentValid = viewModel?.currentUserPaymentBoolean?.value ?: false
        Log.d("TAG", "ChargingStationDetailsFragment-Station availability: ${station.availability} - Payment valid: $isPaymentValid")

        if (station.availability && isPaymentValid) {
            Log.d("TAG", "ChargingStationDetailsFragment-Start charging button clicked")
            val action = ChargingStationDetailsFragmentDirections.actionChargingStationDetailsFragmentToChargingPageFragment()
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "Station unavailable or missing payment info.", Toast.LENGTH_SHORT).show()
        }
    }

    // Bind the station details to the UI
    private fun bindStationDetails(station: ChargingStation) {
        binding?.apply {
            Glide.with(requireContext()).load(station.imageUrl).into(imageView)
            addressTextView.text = station.addressName
            availabilityTextView.text = if (station.availability) "Available" else "Unavailable"
            val colorRes = if (station.availability) R.color.green else R.color.red
            availabilityTextView.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
            chargingSpeedTextView.text = station.chargingSpeed
            priceTextView.text = station.pricePerkW.toString()
        }
    }


    private fun openSmsApp(phoneNumber: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber")).apply {
                putExtra("sms_body", message)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unable to open messaging app.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openWaze() {
        val locationUrl = viewModel?.chargingStation?.value?.wazeUrl ?: return
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Waze app not installed.", Toast.LENGTH_SHORT).show()
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