package com.example.chargehoodapp.presentation.orders.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.data.model.Booking
import com.example.chargehoodapp.databinding.OrdersListRowBinding
import com.example.chargehoodapp.presentation.orders.OrdersListViewModel

class OrdersListAdapter(
    private var ordersList: List<Booking>?,
    private val viewModel: OrdersListViewModel
): RecyclerView.Adapter<OrdersViewHolder>() {

    fun set(orders: List<Booking>?){
        Log.d("TAG", "OrdersListAdapter - Setting new list with ${orders?.size} items")
        this.ordersList = orders
        notifyDataSetChanged()
    }

    //Initialize every row view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OrdersListRowBinding.inflate(inflater, parent, false)
        return OrdersViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ordersList?.size ?: 0
    }

    //Bind data to the view holder
    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        if(ordersList!= null) {
            holder.bind(ordersList!![position], viewModel)
        }
    }
}