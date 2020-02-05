package com.finde.deliveryapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.finde.deliveryapp.models.ParcelModel

class ParcelsViewModel : ViewModel() {
    private val parcels = MutableLiveData<MutableList<ParcelModel>>()

    fun getParcels():LiveData<MutableList<ParcelModel>>{
        return parcels
    }

    fun setParcel(parcelModels: MutableList<ParcelModel>){
        parcels.value = parcelModels
    }

    fun addParcel(parcelModel: ParcelModel){
        parcels.value?.add(parcelModel)
    }
}