package com.example.chargehoodapp.presentation.orders.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chargehoodapp.R
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.Booking
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import com.example.chargehoodapp.databinding.OrdersListRowBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrdersViewHolder(
    private val binding: OrdersListRowBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var booking: Booking? = null
    private val userUid = FirebaseModel.getCurrentUser()?.uid
    private var station: ChargingStation? = null
    private val repository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository


    //Bind the data into the row
    fun bind(booking: Booking) {
        this.booking = booking
        station = repository.getChargingStationById(booking.stationId)
        if (station != null) {
            if(userUid == station?.ownerId){
                Glide.with(binding.root.context)
                    .load(R.drawable.ic_order_plus)
                    .circleCrop()
                    .into(binding.StatusIcon)
            } else{
                Glide.with(binding.root.context)
                    .load(R.drawable.ic_order_minus)
                    .circleCrop()
                    .into(binding.StatusIcon)
            }
        }

        //convert the long date to a readable date
        val date = Date(booking.date)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        binding.stationAddress.text = station?.addressName
        binding.TotalCost.text = booking.chargingCost.toString()
        binding.Date.text = formattedDate
    }

}