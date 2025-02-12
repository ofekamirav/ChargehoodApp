package com.example.chargehoodapp.presentation.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.PaymentInfo
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import com.example.chargehoodapp.data.repository.PaymentInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentMethodViewModel : ViewModel() {

    private val repository: PaymentInfoRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).paymentInfoRepository

    private val _paymentInfoList = MutableLiveData<List<PaymentInfo>>()
    val paymentInfoList: LiveData<List<PaymentInfo>> get() = _paymentInfoList


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        FirebaseModel.addAuthStateListener {
            refreshUserData()
        }
    }


    fun syncPayments() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            val payments = repository.syncPaymentInfo()
            _paymentInfoList.postValue(payments)
            _isLoading.postValue(false)
        }
    }



    fun refreshUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.syncPaymentInfo()
        }
    }


    fun deletePaymentInfo(paymentInfo: PaymentInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePaymentInfo(paymentInfo)
            syncPayments()
        }
    }


}