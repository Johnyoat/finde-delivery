package com.finde.deliveryapp.ext

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.finde.deliveryapp.R

fun Fragment.navigateTo(fragmentId:Int) {
    this.requireActivity().findNavController(R.id.container).navigate(fragmentId)
}

fun Fragment.navigateToWithArgs(fragmentId:Int,args:Bundle) {
    this.requireActivity().findNavController(R.id.container).navigate(fragmentId,args)
}


fun Fragment.popStack(){
    this.requireActivity().findNavController(R.id.container).popBackStack()
}