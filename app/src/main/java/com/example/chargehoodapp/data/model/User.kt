package com.example.chargehoodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chargehoodapp.base.Constants.Collections.USERS

@Entity(tableName = USERS)
data class User(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val profilePictureUrl: String = "",
    val isStationOwner: Boolean = false,
    val hasPaymentInfo: Boolean = false
){
    constructor() : this("", "", "", "", "", false, false)
}
