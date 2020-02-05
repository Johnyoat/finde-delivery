package com.finde.deliveryapp.models

import android.os.Parcel
import android.os.Parcelable

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