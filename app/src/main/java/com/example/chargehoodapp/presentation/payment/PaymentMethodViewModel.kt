package com.example.chargehoodapp.presentation.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chargehoodapp.data.model.PaymentInfo

class PaymentMethodViewModel : ViewModel() {

    private val _paymentInfoList = MutableLiveData<List<PaymentInfo>?>()
    val paymentInfoList: LiveData<List<PaymentInfo>?> get() = _paymentInfoList

    init {
        loadDummyData()
    }

    private fun loadDummyData() {
        val dummyData = listOf(
            PaymentInfo(
                userId = "123",
                cardLastFour = "4242",
                cardType = "Visa",
                cardExpiry = "12/25",
            ),
            PaymentInfo(
                userId = "123",
                cardLastFour = "5678",
                cardType = "MasterCard",
                cardExpiry = "11/24",
            )
        )
        _paymentInfoList.value = dummyData
    }

    fun deletePaymentInfo(paymentInfo: PaymentInfo) {
        val updatedList = _paymentInfoList.value?.toMutableList()
        updatedList?.remove(paymentInfo)
        _paymentInfoList.value = updatedList
    }
}