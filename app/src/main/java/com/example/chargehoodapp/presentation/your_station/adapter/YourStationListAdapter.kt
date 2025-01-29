package com.example.chargehoodapp.presentation.your_station.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.databinding.YourStationsListRowBinding

class YourStationListAdapter(
    private var stations: List<ChargingStation> = listOf(),
    private val listener: OnStationClick
) : RecyclerView.Adapter<YourStationListViewHolder>() {

    fun updateStations(newStations: List<ChargingStation>) {
        stations = newStations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourStationListViewHolder {
        val binding = YourStationsListRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return YourStationListViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = stations.size

    override fun onBindViewHolder(holder: YourStationListViewHolder, position: Int) {
        holder.bind(stations[position])
    }
}

