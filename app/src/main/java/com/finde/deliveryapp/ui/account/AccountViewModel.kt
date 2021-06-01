package com.finde.deliveryapp.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finde.deliveryapp.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception

class AccountViewModel : ViewModel() {
    private val uid = FirebaseAuth.getInstance().uid
    private val db = FirebaseFirestore.getInstance()


    fun getUser(): LiveData<UserModel> {
        val user = MutableLiveData<UserModel>()

        db.document("users/${uid}").addSnapshotListener { userSnapShot, err ->
            if (err == null) {
                user.value = userSnapShot?.toObject(UserModel::class.java)
            }
        }

        return user
    }


    fun updateUser(userModel: UserModel): Task<Void> {
        userModel.uid = uid!!
        return db.document("users/${uid}").set(userModel)
    }

}