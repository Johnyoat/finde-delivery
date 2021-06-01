package com.finde.deliveryapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParcelModel(
    var id: String = "",
    var senderName: String = "",
    var senderContact: String = "",
    var receiverName: String = "",
    var receiverContact: String = "",
    var destinationLat: Double = 0.00,
    var destinationLng: Double = 0.00,
    var originLat: Double = 0.00,
    var originLng: Double = 0.00,
    var parcelType: String = "",
    var parcelWeight: String = "",
    var destination: String = "",
    var origin: String = "",
    var packageImage: String = "",
    var uid: String = "",
    var deliveryCompanyName:String = "",
    var deliveryCompanyId:String = ""
) : Parcelable