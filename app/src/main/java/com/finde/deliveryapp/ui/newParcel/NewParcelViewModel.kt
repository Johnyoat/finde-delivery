package com.finde.deliveryapp.ui.newParcel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finde.deliveryapp.api.APIService
import com.finde.deliveryapp.models.BusinessModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class NewParcelViewModel : ViewModel() {
    private val apiService = APIService.create()

    fun getBusinesses(): LiveData<List<BusinessModel>> {
        val businesses = MutableLiveData<List<BusinessModel>>()

        viewModelScope.launch {
            try {
                businesses.value = apiService.getBusinesses()
            } catch (e: Exception) {
                Timber.d(e.localizedMessage)
            }
        }

        return businesses
    }
}