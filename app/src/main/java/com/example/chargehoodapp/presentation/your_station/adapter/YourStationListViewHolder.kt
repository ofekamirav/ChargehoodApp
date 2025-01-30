package com.example.chargehoodapp.presentation.your_station.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.databinding.YourStationsListRowBinding

class YourStationListViewHolder(
    private val binding: YourStationsListRowBinding,
    private val listener: OnStationClick?,
    private val onDeleteClick: OnDeleteClick?
) : RecyclerView.ViewHolder(binding.root) {

    private var station: ChargingStation? = null

    init{
        binding.deleteCardButton.setOnClickListener {
            station?.let {
                onDeleteClick?.onDeleteClick(it)
            }
        }
        binding.root.setOnClickListener {
            station?.let {
                listener?.onStationClick(it)
            }
        }

    }

    fun bind(station: ChargingStation?, position: Int) {
        this.station = station
        binding.stationAddress.text = station?.addressName
        binding.stationChargingSpeed.text = station?.chargingSpeed
        binding.deleteCardButton.tag = position

    }
}
