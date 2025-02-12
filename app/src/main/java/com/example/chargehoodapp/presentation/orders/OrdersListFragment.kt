package com.example.chargehoodapp.presentation.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.databinding.FragmentOrdersListBinding
import com.example.chargehoodapp.presentation.orders.adapter.OrdersListAdapter

class OrdersListFragment: Fragment() {

    private var binding: FragmentOrdersListBinding?=null
    private var viewModel: OrdersListViewModel?=null
    private var adapter: OrdersListAdapter?= null
    private var recyclerView: RecyclerView?= null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOrdersListBinding.inflate(inflater, container, false)


        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[OrdersListViewModel::class.java]

        recyclerView = binding?.OrdersRecyclerList
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        adapter = OrdersListAdapter(emptyList(), viewModel!!)
        recyclerView?.adapter = adapter

        viewModel?.allRelevantBookings?.observe(viewLifecycleOwner) { bookings ->
            Log.d("TAG", "OrdersListFragment - Updating adapter with ${bookings?.size} items")
            adapter?.set(bookings)
            updateUI(bookings.isNullOrEmpty())
        }

        viewModel?.loadAllRelevantBookings()

        binding?.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    // Update UI based on the list being empty or not
    private fun updateUI(isEmpty: Boolean) {
        binding?.apply {
            OrdersRecyclerList.visibility = if (isEmpty) View.GONE else View.VISIBLE
            noCardsTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}