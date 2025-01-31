package com.example.chargehoodapp.presentation.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chargehoodapp.data.model.PaymentInfo
import com.example.chargehoodapp.databinding.PaymentMethodListRowBinding

class PaymentMethodAdapter(
    private var paymentMethods: List<PaymentInfo>?,
    private val onDeleteClicked: OnDeleteClick
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

    // bind the view holder row
    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        val paymentMethod = paymentMethods?.get(position)
        if (paymentMethod != null) {
            holder.bind(paymentMethod)

            holder.binding.deleteCardButton.setOnClickListener {
                onDeleteClicked.onDeleteClick(paymentMethod)
            }
        }

    }

    override fun getItemCount(): Int = paymentMethods?.size ?: 0

    fun set(newPaymentMethods: List<PaymentInfo>?) {
        this.paymentMethods = newPaymentMethods
        notifyDataSetChanged()
    }
}
