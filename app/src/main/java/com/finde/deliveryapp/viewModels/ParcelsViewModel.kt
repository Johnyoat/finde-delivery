package com.finde.deliveryapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.finde.deliveryapp.models.ParcelModel

class ParcelsViewModel : ViewModel() {
    private val parcels = MutableLiveData<List<ParcelModel>>()

    fun getParcels():LiveData<List<ParcelModel>>{
        return parcels
    }
}