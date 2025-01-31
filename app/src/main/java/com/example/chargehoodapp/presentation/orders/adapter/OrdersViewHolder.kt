package com.example.chargehoodapp.presentation.orders.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chargehoodapp.R
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.Booking
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import com.example.chargehoodapp.databinding.OrdersListRowBinding
import com.example.chargehoodapp.presentation.orders.OrdersListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrdersViewHolder(
    private val binding: OrdersListRowBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val repository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository


    //Bind the data into the row
    fun bind(booking: Booking, viewModel: OrdersListViewModel) {
//        CoroutineScope(Dispatchers.Main).launch {
//            val station = viewModel.getStationById(booking.stationId.toString())
//            if (station != null) {
//                val userUid = FirebaseModel.getCurrentUser()?.uid
//                val iconRes = if (userUid == station.ownerId) R.drawable.ic_order_plus else R.drawable.ic_order_minus
//                Glide.with(binding.root.context).load(iconRes).circleCrop().into(binding.StatusIcon)
//
//                binding.stationAddress.text = station.addressName
//            } else {
//                binding.stationAddress.text = "Unknown Station"
//            }
//        }
//
//        val date = Date(booking.date)
//        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
//
//        val hours = booking.time / 60
//        val minutes = booking.time % 60
//        val formattedTime = String.format("%02d:%02d", hours, minutes)
//
//        binding.TotalCost.text = "${booking.chargingCost}$"
//        binding.Date.text = formattedDate
//        binding.Time.text = formattedTime
    }

}