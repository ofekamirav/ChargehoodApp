package com.example.chargehoodapp.presentation.your_station.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.databinding.YourStationsListRowBinding

class YourStationListViewHolder(
    private val binding: YourStationsListRowBinding,
    private val listener: OnStationClick
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(station: ChargingStation) {
        binding.stationAddress.text = station.addressName
        binding.stationChargingSpeed.text = "Charging Speed: ${station.chargingSpeed}"

        // Handle click event
        binding.root.setOnClickListener { listener.onStationClick(station) }
    }
}
