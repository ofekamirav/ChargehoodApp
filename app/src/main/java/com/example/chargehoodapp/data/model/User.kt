package com.example.chargehoodapp.data.model

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val profilePictureUrl: String = "",
    val isStationOwner: Boolean = false
){
    constructor() : this("", "", "", "", "", false)
}
