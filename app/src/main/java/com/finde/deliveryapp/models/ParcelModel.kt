package com.finde.deliveryapp.models

data class ParcelModel(
    val id:String = "",
    val senderName: String = "",
    val senderContact:String="",
    val receiverName: String = "",
    val receiverContact: String = "",
    val destinationLat: Double = 0.00,
    val destinationLng: Double = 0.00,
    val startLocationLat: Double = 0.00,
    val startLocationLng: Double = 0.00,
    val parcelType:String = "",
    val parcelWeight:String ="",
    val destination:String = "",
    val origin:String = ""
)