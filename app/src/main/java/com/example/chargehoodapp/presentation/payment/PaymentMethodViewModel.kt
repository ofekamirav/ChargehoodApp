package com.example.chargehoodapp.presentation.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.PaymentInfo
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import com.example.chargehoodapp.data.repository.PaymentInfoRepository
import kotlinx.coroutines.launch

class PaymentMethodViewModel : ViewModel() {

    private val repository: PaymentInfoRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).paymentInfoRepository

    //Local list of payment methods
    val paymentInfoList: LiveData<List<PaymentInfo>> = repository.getPaymentMethods()

    fun syncPayments() {
        viewModelScope.launch {
            repository.syncPaymentInfo()
        }
    }

    fun deletePaymentInfo(paymentInfo: PaymentInfo) {
        viewModelScope.launch {
            repository.deletePaymentInfo(paymentInfo)
        }
    }

}