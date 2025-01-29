package com.example.chargehoodapp.presentation.payment.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chargehoodapp.R
import com.example.chargehoodapp.data.model.PaymentInfo
import com.example.chargehoodapp.databinding.PaymentMethodListRowBinding

class PaymentMethodViewHolder(
    private val binding: PaymentMethodListRowBinding,
    private val onDeleteClicked: (PaymentInfo) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(paymentInfo: PaymentInfo) {
        // Set card type icon
        val cardTypeIcon = when (paymentInfo.cardType) {
            "Visa" -> R.drawable.ic_visa
            "MasterCard" -> R.drawable.ic_mastercard
            "AmericanExpress" -> R.drawable.ic_american_express
            else -> R.drawable.ic_payments
        }
        Glide.with(binding.root.context)
            .load(cardTypeIcon)
            .into(binding.cardTypeIcon)

        // Set last 4 digits and expiry date
        binding.cardLast4.text = "•••• ${paymentInfo.cardLastFour}"
        binding.expiryDate.text = "Expiry: ${paymentInfo.cardExpiry}"

        // Handle delete button click
        binding.deleteCardButton.setOnClickListener {
            onDeleteClicked(paymentInfo)
        }
    }
}
