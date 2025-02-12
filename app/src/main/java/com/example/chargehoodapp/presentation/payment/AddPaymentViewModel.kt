package com.example.chargehoodapp.presentation.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.PaymentInfo
import com.example.chargehoodapp.data.repository.PaymentInfoRepository
import FirebaseModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

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
        _selectedCardType.postValue(cardType)
    }

    fun resetMessages() {
        _successMessage.postValue(null)
        _errorMessage.postValue(null)
    }


    fun savePaymentInfo(cardNumber: String, expiry: String) {
        if (cardNumber.length < 16) {
            val error = "Invalid Card Number (Must be 16 digits)"
            _errorMessage.postValue(error)
            return
        }

        if (!expiry.matches(Regex("^(0[1-9]|1[0-2])/[0-9]{2}$"))) {
            _errorMessage.postValue("Invalid Expiry Date (Format: MM/YY)")
            return
        }
        if(_selectedCardType.value == null){
            _errorMessage.postValue("Please select a card type")
            return
        }

        val paymentData = PaymentInfo(
            userId = userId,
            cardLastFour = cardNumber.takeLast(4),
            cardType = _selectedCardType.value ?: "Unknown",
            cardExpiry = expiry
        )

        viewModelScope.launch {
            try {
                paymentRepository.addPaymentInfo(paymentData)
                _successMessage.postValue("Card Saved Successfully!")
                Log.d("TAG", "AddPaymentViewModel-Card Saved Successfully!")
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to save card: ${e.message}")
                Log.d("TAG", "AddPaymentViewModel-Failed to save card: ${e.message}")
            }
        }
    }
}
