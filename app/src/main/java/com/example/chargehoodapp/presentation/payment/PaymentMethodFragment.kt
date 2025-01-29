package com.example.chargehoodapp.presentation.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chargehoodapp.data.model.PaymentInfo
import com.example.chargehoodapp.databinding.FragmentPaymentMethodBinding
import com.example.chargehoodapp.presentation.payment.adapter.PaymentMethodAdapter

class PaymentMethodFragment : Fragment() {

    private var binding: FragmentPaymentMethodBinding? = null
    val paymentInfoList = MutableLiveData<List<PaymentInfo>?>()

    private lateinit var adapter: PaymentMethodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentMethodBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize adapter
        adapter = PaymentMethodAdapter(emptyList()) { paymentInfo ->
            deletePaymentInfo(paymentInfo)
        }

        // Set up RecyclerView
        binding?.recyclerViewPaymentMethods?.layoutManager = LinearLayoutManager(requireContext())
        binding?.recyclerViewPaymentMethods?.adapter = adapter

        // Observe paymentInfoList LiveData
        paymentInfoList.observe(viewLifecycleOwner) { paymentMethods ->
            adapter.updateData(paymentMethods)
            updateUI(paymentMethods?.isEmpty() ?: true)
        }

        // Add dummy data (replace with Room or Firebase fetch later)
        loadDummyData()

        // Handle "Add Card" button
        binding?.addCardButton?.setOnClickListener {
            // Navigate to "Add Card" screen or handle card addition logic
        }
    }

    private fun loadDummyData() {
        val dummyData = listOf(
            PaymentInfo(
                id = "1",
                userId = "123",
                cardLastFour = "4242",
                cardType = "Visa",
                cardExpiry = "12/25"
            ),
            PaymentInfo(
                id = "2",
                userId = "123",
                cardLastFour = "5678",
                cardType = "MasterCard",
                cardExpiry = "11/24"
            )
        )
        paymentInfoList.value = dummyData
    }

    private fun deletePaymentInfo(paymentInfo: PaymentInfo) {
        val updatedList = paymentInfoList.value?.toMutableList()
        updatedList?.remove(paymentInfo)
        paymentInfoList.value = updatedList
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
