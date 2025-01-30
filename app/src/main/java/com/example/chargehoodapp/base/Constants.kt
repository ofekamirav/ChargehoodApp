package com.example.chargehoodapp.base

import com.example.chargehoodapp.data.model.PaymentInfo


typealias EmptyCallback = () -> Unit
typealias StringCallback = (String?) -> Unit
object Constants {

    object Collections {
        const val USERS = "users"
        const val CHARGING_STATIONS = "charging_stations"
        const val PAYMENT_INFO = "payment_info"
        const val BOOKING = "booking"
    }


}