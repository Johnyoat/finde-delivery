package com.finde.deliveryapp.models

import android.os.Parcel
import android.os.Parcelable

data class ParcelModel(
    var id:String = "",
    var senderName: String = "",
    var senderContact:String="",
    var receiverName: String = "",
    var receiverContact: String = "",
    var destinationLat: Double = 0.00,
    var destinationLng: Double = 0.00,
    var startLocationLat: Double = 0.00,
    var startLocationLng: Double = 0.00,
    var parcelType:String = "",
    var parcelWeight:String ="",
    var destination:String = "",
    var origin:String = ""
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(senderName)
        parcel.writeString(senderContact)
        parcel.writeString(receiverName)
        parcel.writeString(receiverContact)
        parcel.writeDouble(destinationLat)
        parcel.writeDouble(destinationLng)
        parcel.writeDouble(startLocationLat)
        parcel.writeDouble(startLocationLng)
        parcel.writeString(parcelType)
        parcel.writeString(parcelWeight)
        parcel.writeString(destination)
        parcel.writeString(origin)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelModel> {
        override fun createFromParcel(parcel: Parcel): ParcelModel {
            return ParcelModel(parcel)
        }

        override fun newArray(size: Int): Array<ParcelModel?> {
            return arrayOfNulls(size)
        }
    }
}