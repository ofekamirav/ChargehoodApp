package com.example.chargehoodapp.presentation.your_station.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.databinding.YourStationsListRowBinding

class YourStationListAdapter(
    private var stations: List<ChargingStation>?,
    var listener: OnStationClick? = null,
    private val onDeleteClick: OnDeleteClick? = null
) : RecyclerView.Adapter<YourStationListViewHolder>() {

    private var DeleteClick: OnDeleteClick? = null

    fun setStations(newStations: List<ChargingStation>?) {
        this.stations = newStations
        notifyDataSetChanged()
    }

    fun setOnDeleteClick(onDeleteClick: OnDeleteClick) {
        this.DeleteClick = onDeleteClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourStationListViewHolder {
        val binding = YourStationsListRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return YourStationListViewHolder(binding, listener, onDeleteClick)
    }

    override fun getItemCount(): Int = stations?.size ?: 0

    override fun onBindViewHolder(holder: YourStationListViewHolder, position: Int) {
        val station = stations?.get(position)
        holder.bind(station, position)
    }
}

