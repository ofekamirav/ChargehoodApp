package com.example.chargehoodapp.presentation.charging_page

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chargehoodapp.BuildConfig
import com.example.chargehoodapp.R
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.databinding.FragmentChargingPageBinding


class ChargingPageFragment : Fragment() {

    private val viewModel: ChargingPageViewModel by activityViewModels()

    private var binding: FragmentChargingPageBinding? = null
    private var LocationUrl: String? = null
    private var isChargingStarted = false
    private var apiKey: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyApplication.Globals.selectedStation?.let { station ->
            viewModel.setStation(station)
            if (!isChargingStarted) {
                viewModel.startCharging()
                isChargingStarted = true
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding?.FirstprogressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding?.contentGroup?.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        //Set the timer and progress bar
        viewModel.formattedTime.observe(viewLifecycleOwner) { formattedTime ->
            binding?.timerTextView?.text = formattedTime
        }

        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            binding?.progressBar?.let { progressBar ->
                val animator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, progress)
                animator.duration = 500
                animator.start()
            }
        }


        //Set the static map view
        viewModel.station.observe(viewLifecycleOwner){ station ->
            val lat = station.latitude
            val long = station.longitude
            if (lat != null && long != null) {
                LocationUrl = "https://maps.googleapis.com/maps/api/staticmap?" +
                        "center=$lat,$long&zoom=15&size=600x300&maptype=roadmap" +
                        "&markers=color:0x77BFBE|$lat,$long&key=$apiKey"
                Log.d("TAG", "ChargingPageFragment - LocationUrl: $LocationUrl")
            }
            if (LocationUrl != null) {
                Glide.with(this)
                    .load(LocationUrl)
                    .placeholder(R.drawable.default_station_pic)
                    .into(binding?.staticMapImageView!!)
            }
        }

        binding?.StopButton?.setOnClickListener {
            viewModel.stopCharging()
            val action = ChargingPageFragmentDirections.actionChargingPageFragmentToHomepageFragment()
            findNavController().navigate(action)
        }
        binding?.chargingPowerTextView?.text = "${viewModel.station.value?.chargingSpeed} kW"
        binding?.connectionTypeTextView?.text = viewModel.station.value?.connectionType
        binding?.priceTextView?.text = "${viewModel.station.value?.pricePerkW} $"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChargingPageBinding.inflate(inflater, container, false)

        apiKey = BuildConfig.GOOGLE_MAP_API_KEY
        return binding?.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}