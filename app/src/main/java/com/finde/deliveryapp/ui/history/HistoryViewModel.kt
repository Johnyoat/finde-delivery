package com.finde.deliveryapp.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.finde.deliveryapp.models.ParcelModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid

    fun getHistory():MutableLiveData<List<ParcelModel>> {
        val parcels = MutableLiveData<List<ParcelModel>>()

        db.collection("deliveries").whereEqualTo("uid",uid).addSnapshotListener { deliveryDocs, error ->
            if (error == null){
                parcels.value = deliveryDocs?.toObjects(ParcelModel::class.java)
            }
        }
        return parcels
    }
}