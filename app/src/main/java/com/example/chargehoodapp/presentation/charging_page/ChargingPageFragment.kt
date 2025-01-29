package com.example.chargehoodapp.presentation.charging_page

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chargehoodapp.R
import com.example.chargehoodapp.databinding.FragmentChargingPageBinding
import com.example.chargehoodapp.utils.extensions.MapUtils
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView

class ChargingPageFragment : Fragment() {

    private var binding: FragmentChargingPageBinding? = null
    private var viewModel: ChargingPageViewModel? = null
    private var LocationUrl: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity())[ChargingPageViewModel::class.java]

        //Set the timer and progress bar
        viewModel?.elapsedMinutes?.observe(viewLifecycleOwner) { minutes ->
            val hours = minutes / 60
            val mins = minutes % 60
            val formattedTime = String.format("%02d:%02d", hours, mins)
            binding?.timerTextView?.text = formattedTime
        }
        viewModel?.progress?.observe(viewLifecycleOwner) { progress ->
            binding?.progressBar?.progress = progress
        }

        //Set the static map view
        viewModel?.booking?.observe(viewLifecycleOwner){ booking ->
            val lat = viewModel?.station?.value?.latitude
            val long = viewModel?.station?.value?.longitude
            if (lat != null && long != null) {
                LocationUrl = MapUtils.getStaticMapUrl(lat.toDouble(), long.toDouble())
            }
            if (LocationUrl != null) {
                Glide.with(this)
                    .load(LocationUrl)
                    .placeholder(R.drawable.default_station_pic)
                    .into(binding?.staticMapImageView!!)
            }
        }

        viewModel?.energyCharged?.observe(viewLifecycleOwner) { energy ->
            binding?.energyChargedTextView?.text = "$energy kWh"
        }

        viewModel?.chargingCost?.observe(viewLifecycleOwner) { cost ->
            binding?.costTextView?.text = "$$cost"
        }

        binding?.StopButton?.setOnClickListener {
            viewModel?.stopCharging()
            val action = ChargingPageFragmentDirections.actionChargingPageFragmentToHomepageFragment()
            findNavController().navigate(action)
        }



    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChargingPageBinding.inflate(inflater, container, false)


        return binding?.root
    }


    fun setPageDetails(){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}