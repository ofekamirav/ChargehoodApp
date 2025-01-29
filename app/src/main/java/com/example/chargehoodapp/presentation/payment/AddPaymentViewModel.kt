package com.example.chargehoodapp.presentation.payment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.PaymentInfo
import com.example.chargehoodapp.data.repository.PaymentInfoRepository
import FirebaseModel

class AddPaymentViewModel : ViewModel() {

    private val userId = FirebaseModel.getCurrentUser()?.uid ?: "Unknown"

    private val paymentRepository: PaymentInfoRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).paymentInfoRepository

    private val _selectedCardType: MutableLiveData<String?> = MutableLiveData<String?>()

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> get() = _successMessage

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun selectCard(cardType: String) {
        _selectedCardType.value = cardType
    }

    fun savePaymentInfo(cardNumber: String, expiry: String) {
        if (cardNumber.length < 16) {
            _errorMessage.value = "Invalid Card Number (Must be 16 digits)"
            return
        }

        if (!expiry.matches(Regex("^(0[1-9]|1[0-2])/[0-9]{2}$"))) {
            _errorMessage.value = "Invalid Expiry Date (Format: MM/YY)"
            return
        }

        val paymentData = PaymentInfo(
            userId = userId,
            cardLastFour = cardNumber.takeLast(4),
            cardType = _selectedCardType.value ?: "Unknown",
            cardExpiry = expiry
        )

        paymentRepository.addPaymentInfo(paymentData)
        _successMessage.value = "Card Saved Successfully!"
    }
}
