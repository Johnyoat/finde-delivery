package com.finde.deliveryapp.ext

import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.finde.deliveryapp.R
import de.hdodenhof.circleimageview.CircleImageView

fun Fragment.navigateTo(fragmentId: Int) {
    this.requireActivity().findNavController(R.id.container).navigate(fragmentId)
}

fun Fragment.navigateToWithArgs(fragmentId: Int, args: Bundle) {
    this.requireActivity().findNavController(R.id.container).navigate(fragmentId, args)
}


fun Fragment.popStack() {
    this.requireActivity().findNavController(R.id.container).popBackStack()
}


fun areEditTextsValid(vararg editTexts: AppCompatEditText): Boolean {
    val args = editTexts.size
    var counter = args

    editTexts.forEach { editText ->
        if (editText.text!!.isEmpty() || editText.text!!.length < 4) {
            editText.error = "Invalid field"
            counter--
        }
    }
    return args == counter
}


fun AppCompatImageView.load(context: Context, url: String) {
    Glide.with(context).load(url).into(this)
}
fun CircleImageView.load(context: Context, url: String) {
    Glide.with(context).load(url).into(this)
}