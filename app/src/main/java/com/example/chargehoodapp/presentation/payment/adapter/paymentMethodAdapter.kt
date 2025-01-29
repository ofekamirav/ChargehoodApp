package com.example.chargehoodapp.presentation.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.data.model.PaymentInfo
import com.example.chargehoodapp.databinding.PaymentMethodListRowBinding

class PaymentMethodAdapter(
    private var paymentMethods: List<PaymentInfo>,
    private val onDeleteClicked: (PaymentInfo) -> Unit
) : RecyclerView.Adapter<PaymentMethodViewHolder>() {

    // initiate the view holder row
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val binding = PaymentMethodListRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PaymentMethodViewHolder(binding, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        holder.bind(paymentMethods[position])
    }

    override fun getItemCount(): Int = paymentMethods.size

    fun updateData(newPaymentMethods: List<PaymentInfo>?) {
        paymentMethods = newPaymentMethods ?: emptyList()
        notifyDataSetChanged()
    }
}
