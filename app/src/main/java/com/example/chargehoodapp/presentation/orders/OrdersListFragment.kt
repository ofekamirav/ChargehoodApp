package com.example.chargehoodapp.presentation.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        viewModel?.fetchOrders()

        recyclerView = binding?.OrdersRecyclerList
        recyclerView?.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager

        adapter = OrdersListAdapter(emptyList())
        recyclerView?.adapter = adapter

        viewModel?.orders?.observe(viewLifecycleOwner) { orders ->
            adapter?.set(orders)
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun GetAllOrders(){

    }
}