package com.finde.deliveryapp.ui.confirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.finde.deliveryapp.R
import com.finde.deliveryapp.databinding.FragmentConfirmationBinding

class ConfirmationFragment(private val isSuccess:Boolean,private val trackingId:String) : DialogFragment() {
    private lateinit var binding: FragmentConfirmationBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        binding = FragmentConfirmationBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding = FragmentConfirmationBinding.bind(view)

        binding.closeBtnImg.setOnClickListener {
            dismissAllowingStateLoss()
        }

        binding.closeBtn.setOnClickListener {
            dismissAllowingStateLoss()
        }


        if (isSuccess){
            binding.subHeader.text = "Tracking Id: $trackingId"
        }else{
            binding.title.text = "Order Failed!"
            binding.subHeader.text = "Order could not be placed, please try again later!"
            binding.animator.setAnimation("error.json")
        }


    }


    override fun getTheme(): Int {
        return R.style.AppTheme
    }

}