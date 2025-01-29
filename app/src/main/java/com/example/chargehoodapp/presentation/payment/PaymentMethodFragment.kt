package com.example.chargehoodapp.presentation.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chargehoodapp.R
import com.example.chargehoodapp.databinding.FragmentPaymentMethodBinding
import com.example.chargehoodapp.presentation.payment.adapter.PaymentMethodAdapter

class PaymentMethodFragment : Fragment() {

    private var binding: FragmentPaymentMethodBinding? = null
    private var viewModel: PaymentMethodViewModel ?= null
    private lateinit var adapter: PaymentMethodAdapter

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
        adapter = PaymentMethodAdapter(emptyList()) { paymentInfo ->
            viewModel?.deletePaymentInfo(paymentInfo)
        }

        binding?.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        binding?.addCardButton?.setOnClickListener {
            findNavController().navigate(R.id.action_paymentMethodFragment_to_addPaymentFragment)
        }

        // Set up RecyclerView
        binding?.recyclerViewPaymentMethods?.layoutManager = LinearLayoutManager(requireContext())
        binding?.recyclerViewPaymentMethods?.adapter = adapter

        // Observe ViewModel LiveData
        viewModel?.paymentInfoList?.observe(viewLifecycleOwner) { paymentMethods ->
            adapter.updateData(paymentMethods)
            updateUI(paymentMethods?.isEmpty() ?: true)
        }

        // Handle "Add Card" button (Navigate to Add Card screen)
        binding?.addCardButton?.setOnClickListener {
            findNavController().navigate(R.id.action_paymentMethodFragment_to_addPaymentFragment)
        }
    }

    private fun updateUI(isEmpty: Boolean) {
        binding?.apply {
            recyclerViewPaymentMethods.visibility = if (isEmpty) View.GONE else View.VISIBLE
            noCardsTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
