package com.example.chargehoodapp.presentation.payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.data.model.PaymentInfo
import com.example.chargehoodapp.databinding.FragmentPaymentMethodBinding
import com.example.chargehoodapp.presentation.payment.adapter.PaymentMethodAdapter
import com.example.chargehoodapp.presentation.payment.adapter.OnDeleteClick

class PaymentMethodFragment : Fragment() {

    private var binding: FragmentPaymentMethodBinding? = null
    private var viewModel: PaymentMethodViewModel ?= null
    private var adapter: PaymentMethodAdapter?=null
    private var recyclerView: RecyclerView?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentMethodBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[PaymentMethodViewModel::class.java]

        // Initialize adapter
        recyclerView = binding?.recyclerViewPaymentMethods
        recyclerView?.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager

        adapter = PaymentMethodAdapter(emptyList(), object : OnDeleteClick {
            override fun onDeleteClick(paymentMethod: PaymentInfo) {
                showDialogDelete(paymentMethod)
            }
        })

        recyclerView?.adapter = adapter

        //Observe the LiveData
        viewModel?.paymentInfoList?.observe(viewLifecycleOwner) { paymentMethods ->
            Log.d("TAG", "PaymentMethodsFragment-UI - Received ${paymentMethods.size} payment methods")
            adapter?.set(paymentMethods)
            updateUI(paymentMethods.isEmpty())
        }

        viewModel?.isLoading?.observe(viewLifecycleOwner) { isLoading ->
            binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel?.syncPayments()

        binding?.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        binding?.addCardButton?.setOnClickListener {
            val action = PaymentMethodFragmentDirections.actionPaymentMethodFragmentToAddPaymentFragment()
            findNavController().navigate(action)
        }


    }

    // Update UI based on the list being empty or not
    private fun updateUI(isEmpty: Boolean) {
        binding?.apply {
            recyclerViewPaymentMethods.visibility = if (isEmpty) View.GONE else View.VISIBLE
            noCardsTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun showDialogDelete(paymentMethod: PaymentInfo) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Card")
            .setMessage("Are you sure you want to delete this card?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel?.deletePaymentInfo(paymentMethod)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
