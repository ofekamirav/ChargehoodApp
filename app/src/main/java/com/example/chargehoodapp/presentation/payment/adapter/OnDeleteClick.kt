package com.example.chargehoodapp.presentation.payment.adapter

import com.example.chargehoodapp.data.model.PaymentInfo

interface OnDeleteClick {
    fun onDeleteClick(paymentMethod: PaymentInfo)

}