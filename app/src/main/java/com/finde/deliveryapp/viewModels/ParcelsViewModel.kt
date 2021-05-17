package com.finde.deliveryapp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.finde.deliveryapp.models.ParcelModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ParcelsViewModel : ViewModel() {
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

    fun add(parcelModel: ParcelModel): Task<Void> {
       val collectionRef = db.collection("deliveries")
       parcelModel.id = collectionRef.document().id
        parcelModel.uid = uid!!
       return db.document("deliveries/${parcelModel.id}").set(parcelModel)
    }
}