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

    //Local list of payment methods
    val paymentInfoList: LiveData<List<PaymentInfo>> = repository.getPaymentMethodsSync()



    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun syncPayments() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            repository.syncPaymentInfo()
            _isLoading.postValue(false)
        }
    }


    fun deletePaymentInfo(paymentInfo: PaymentInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePaymentInfo(paymentInfo)
        }
    }

}