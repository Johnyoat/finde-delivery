package com.finde.deliveryapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeliveryTimeLineModel(
    var title: String = "",
    var createdAt: Long = 0,
    var status: Boolean = false
):Parcelable
